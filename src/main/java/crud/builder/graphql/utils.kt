package crud.builder.graphql

import org.apache.commons.lang3.text.WordUtils.capitalize

fun toTypeName(entityName: String): String = capitalize(entityName)

fun createMutationName(entityName: String): String = "add${capitalize(entityName)}"

fun getIdFieldName(entityName: String): String = "${entityName}Id"

fun getIdsFieldName(entityName: String): String = "${entityName}Ids"
