package atoz.oop

import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

// `val x by d` hands the getter (and setter, for var) to a delegate object.
// The standard library ships several:
// - Delegates.observable(initial) { prop, old, new -> } runs after each change
// - Delegates.vetoable(initial) { prop, old, new -> Boolean } can reject one
// - Delegates.notNull() is lateinit for types lateinit can't take, like Int
// - a Map can back properties by name, handy for parsed config or JSON
// Your own delegate implements ReadWriteProperty (or just getValue/setValue
// operators). `by lazy` from oop/Properties is a delegate too.

class AuditedSetting(private val log: MutableList<String>) {
    var level: String by Delegates.observable("INFO") { property, old, new ->
        log += "${property.name}: $old -> $new"
    }

    var maxConnections: Int by Delegates.vetoable(10) { _, _, new -> new in 1..100 }

    var port: Int by Delegates.notNull()
}

class ServiceConfig(values: Map<String, Any?>) {
    val name: String by values
    val replicas: Int by values
}

class Trimmed : ReadWriteProperty<Any?, String> {
    private var value = ""
    override fun getValue(thisRef: Any?, property: KProperty<*>): String = value
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        this.value = value.trim()
    }
}

class SignupForm {
    var email: String by Trimmed()
}

fun main() {
    val log = mutableListOf<String>()
    val setting = AuditedSetting(log)
    setting.level = "DEBUG"
    setting.level = "WARN"
    println(log)
    setting.maxConnections = 500
    println("maxConnections after vetoed 500: ${setting.maxConnections}")
    println("port before set -> ${runCatching { setting.port }.exceptionOrNull()}")

    val config = ServiceConfig(mapOf("name" to "api", "replicas" to 3))
    println("${config.name} x${config.replicas}")

    val form = SignupForm().apply { email = "  ian@example.com  " }
    println("[${form.email}]")
}
