import org.postgresql.ds.PGSimpleDataSource
import org.testcontainers.containers.PostgreSQLContainer
import scalasql.core.DbClient
import scalasql.core.TypeMapper
import scalasql.Config
import scalasql.PostgresDialect.*
import scalasql.Sc

object PostgresExample {

  // The example PostgreSQLContainer comes from the library `org.testcontainers:postgresql:1.20.4`
  private lazy val postgres: PostgreSQLContainer[Nothing] = {
    println("Initializing Postgres")
    val pg = new PostgreSQLContainer("postgres:15-alpine")
    pg.start()
    pg
  }

  def main(args: Array[String]): Unit = {
    val postgresClient = getPostgresClient

    runExampleWithArrays(postgresClient)
  }

  private def runExampleWithArrays(postgresClient: DbClient.DataSource): Vector[Product] = {
    postgresClient.transaction { db =>
      //store_ids maybe not the best example of a column that should be an array, but it's a simple example
      db.updateRaw("""
      CREATE TABLE products (
          id SERIAL PRIMARY KEY,
          kebab_case_name VARCHAR(256),
          name VARCHAR(256),
          price DECIMAL(20, 2),
          store_ids BIGINT[] NOT NULL DEFAULT '{}'
      );
      """)

      // We need the given type mapper for Vector[Long] in scope for any operations involving a table with a
      // Vector[Long]
      import TypeMapperImpl.given

      val inserted = db.run(
        Products.insert.batched(_.kebabCaseName, _.name, _.price, _.storeIds)(
          ("face-mask", "Face Mask", 8.88, Vector(1L)),
          ("guitar", "Guitar", 300, Vector(1L, 2L)),
          ("socks", "Socks", 3.14, Vector(2L, 3L)),
          ("skate-board", "Skate Board", 123.45, Vector(3L)),
          ("camera", "Camera", 1000.00, Vector(1L, 3L)),
          ("cookie", "Cookie", 0.10, Vector(2L))
        )
      )

      assert(inserted == 6)

      // For any special operations exclusive to Vectors, we have to implement an extension for Expr[Vector[T]], where T
      // is the target type of the Vector, in this case Long.  We implemented the contains method in VectorOps,
      // and we bring it into scope here
      import VectorOps.contains
      val result =
        db.run(
          Products.select
            .filter(x => x.price > 10 && x.storeIds.contains(1L))
            .sortBy(_.price)
            .desc
        ).toVector

      println(s"Result is $result")
      result
    }
  }

  private def getPostgresClient: DbClient.DataSource = {
    val dataSource = new PGSimpleDataSource
    dataSource.setURL(postgres.getJdbcUrl)
    dataSource.setDatabaseName(postgres.getDatabaseName);
    dataSource.setUser(postgres.getUsername);
    dataSource.setPassword(postgres.getPassword);

    new DbClient.DataSource(dataSource, config = new Config {})
  }

}
