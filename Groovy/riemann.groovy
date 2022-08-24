//an example of Riemann integration
import com.numericalmethod.suanshu.analysis.function.rn2r1.*
import com.numericalmethod.suanshu.analysis.integration.univariate.riemann.*

I = new Riemann()

v = I.integrate(
['evaluate' : {x -> return x * x}] as UnivariateRealFunction,//integrate the function y = x^2
0, 1);//integral range from 0 to 1
