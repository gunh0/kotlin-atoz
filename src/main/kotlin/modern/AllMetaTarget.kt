package atoz.modern

/*
Annotation use-site targets on constructor properties (both stable in 2.4).
`class Signup(@Redacted val password: String)` declares a parameter, a
property, a field and a getter at once: which one gets the annotation?
- New default rule: the parameter, plus the property if the annotation
  allows it, otherwise the field. Before 2.4 it was the parameter only (2.3
  warned about the change), so a Java-style tool that reads *fields*, like
  many serializers and loggers, never saw the annotation.
- `@all:Redacted` puts it on every applicable element, getter included.
- An explicit target (`@param:`, `@field:`, `@get:`) still means exactly that.
Java records have a similar rule: a component annotation propagates to the
parameter, field and accessor where its @Target allows.
*/

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Redacted

class SignupRequest(
    val email: String,
    @Redacted val password: String,
    @all:Redacted val recoveryCode: String,
    @param:Redacted val pin: String, // explicit target: parameter only
)

// A field-based dumper, like a reflection logger or a serializer that reads fields.
fun redactedDump(obj: Any): String =
    obj.javaClass.declaredFields.filterNot { it.isSynthetic }.sortedBy { it.name }.joinToString(", ") { field ->
        field.trySetAccessible()
        "${field.name}=${if (field.isAnnotationPresent(Redacted::class.java)) "****" else field.get(obj)}"
    }

// Where did @Redacted end up for one constructor property? (`index`: its parameter position;
// JVM parameter names aren't kept unless compiled with -java-parameters)
fun redactedOn(type: Class<*>, property: String, index: Int): List<String> = buildList {
    if (type.declaredConstructors.single().parameterAnnotations[index].any { it is Redacted }) add("param")
    if (type.getDeclaredField(property).isAnnotationPresent(Redacted::class.java)) add("field")
    val getter = type.getMethod("get" + property.replaceFirstChar(Char::uppercaseChar))
    if (getter.isAnnotationPresent(Redacted::class.java)) add("getter")
}

fun main() {
    val form = SignupRequest("a@example.com", "hunter2", "RC-7781", "4321")
    println(redactedDump(form))
    listOf("password", "recoveryCode", "pin").forEachIndexed { i, p ->
        println("$p -> ${redactedOn(SignupRequest::class.java, p, i + 1)}")
    }
}
