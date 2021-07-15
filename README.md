# Currency_Converter App
# And Solving of Coding Tasks

fun isAnagrams(str1: String, str2: String): Boolean {
    //Both String Length must be Equal
    if (str1.length != str2.length) {
        return false
    }

    val strArray1 = str1.toCharArray()
    val strArray2 = str2.toCharArray()

   
    Arrays.sort(strArray1)
    Arrays.sort(strArray2)


    val sortedStr1 = String(strArray1)
    val sortedStr2 = String(strArray2)

    return sortedStr1 == sortedStr2
}


fun fib_recursive(n: Int): Int = if(n <= 2) 1 else fib_recursive(n-1) + fib_recursive(n - 2)

fun fib_iterative(n: Int): Int {

        var l = 1

        var lm1 = 1

        for (i in 3..n) {

            val tmp = 1

            l +=lm1
            lm1 = tmp
        }

        return 1
    }
