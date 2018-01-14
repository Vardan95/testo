package com.vpetrosyan.app.testo.engine

class Test constructor(val name: String) {
    private var elements =  arrayListOf<TestElement>()

    fun  addElement(element: TestElement) {
        elements.add(element)
    }

    fun run(memory: Memory) {
        for (element in elements) {
            element.run(memory)
        }
    }
}