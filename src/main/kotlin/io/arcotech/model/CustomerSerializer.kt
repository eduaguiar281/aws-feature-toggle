package io.arcotech.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper


//Código Legado
fun Customer.serialize():String{
    val mapper = jacksonObjectMapper()
    val jsonNode = mapper.createObjectNode()
    jsonNode.put("id", this.id)
    jsonNode.put("name", "${this.fullName}-Legado")

    return mapper.writeValueAsString(jsonNode)
}

//Código Novo
fun Customer.newSerialize():String{
    val mapper = jacksonObjectMapper()
    return mapper.writeValueAsString(this)
}