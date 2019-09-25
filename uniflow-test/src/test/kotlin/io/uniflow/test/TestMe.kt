package io.uniflow.test


class TestMe {
}

class MyFlow : AbstractFlow(){

    fun myTest(){
        makeFlow {
            setState()
        }
    }
}

abstract class AbstractFlow {

}

fun MyFlow.makeFlow(flow : Flow.() -> Unit){
    flow(Flow())
}

class Flow {
    fun setState() = Unit
}