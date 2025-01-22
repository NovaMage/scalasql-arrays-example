import scalasql.core.Expr
import scalasql.core.SqlStr.SqlStringSyntax

//noinspection SqlNoDataSourceInspection
object VectorOps {

  extension [T](v: Expr[Vector[T]]) {

    /** Postgresql ANY function returns true if any element of the argument array satisfies the boolean operator applied
      * to it.  The left hand side should be the value that is being tested.
      * @param x Value that will be searched for in the array
      * @return
      */
    def contains(x: Expr[T]): Expr[Boolean] = Expr { implicit ctx => sql"($x = ANY($v))" }

  }

}
