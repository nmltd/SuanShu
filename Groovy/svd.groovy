//an example of linear algebra
import com.numericalmethod.suanshu.matrix.doubles.dense.*
import com.numericalmethod.suanshu.matrix.doubles.dense.operation.*
import com.numericalmethod.suanshu.matrix.doubles.dense.operation.factorization.*

data = [[1, 2, -1], [0.5, 3, 4], [1.1, -6.2, 0.1]] as double[][]
m1 = new DenseMatrix(data)
m2 = new Inverse(m1)
m3 = m1.multiply(m2) //check if they multiply to get the identity matrix
println m3

m4 = new SVD(m1, true)

U = m4.U()
V = m4.V()
D = m4.D()

m4 = U.multiply(D).multiply(V.t())//check if we get back the original matrix, m1
println m4
