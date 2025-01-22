import scalasql.core.TypeMapper
import scalasql.dialects.PostgresDialect.ExprQueryable
import scalasql.Sc
import scalasql.Table
import TypeMapperImpl.given TypeMapper[Vector[Long]]

//Type alias to avoid leaking scalasql necessary generics throughout the codebase
type Product = Products[Sc]

case class Products[T[_]](
  id: T[Int],
  kebabCaseName: T[String],
  name: T[String],
  price: T[Double],
  storeIds: T[Vector[Long]]
)

object Products extends Table[Products]
