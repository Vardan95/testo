package com.vpetrosyan.app.testo.engine.error

/**
 * Created by Vardan on 1/13/2018.
 */
class TestError constructor(memory: String, operation : String, pos_i: Int, pos_j: Int, failure: String) :
        Exception("op $operation on " + memory + String.format("[%d][%d]", pos_i, pos_j) + " with " +
                    failure)