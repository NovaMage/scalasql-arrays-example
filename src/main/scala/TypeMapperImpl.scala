import java.sql.JDBCType
import java.sql.PreparedStatement
import java.sql.ResultSet

import scalasql.core.TypeMapper

object TypeMapperImpl {

  given TypeMapper[Vector[Long]] = new TypeMapper[Vector[Long]]:

    override def jdbcType: JDBCType = JDBCType.ARRAY

    override def get(r: ResultSet, idx: Int): Vector[Long] =
      //Retrieved arrays are objects (java.lang.Long), not primitives, so we must map them to scala.Long
      r.getArray(idx).getArray.asInstanceOf[Array[java.lang.Long]].map(_.longValue()).toVector

    override def put(r: PreparedStatement, idx: Int, v: Vector[Long]): Unit =
      //BIGINT is the native type that matches scala.Long
      r.setArray(idx, r.getConnection.createArrayOf("BIGINT", v.toArray))

}
