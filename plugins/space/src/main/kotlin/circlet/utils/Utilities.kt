package circlet.utils

import com.intellij.notification.*
import com.intellij.openapi.*
import com.intellij.openapi.application.*
import com.intellij.openapi.components.*
import com.intellij.openapi.project.*
import com.intellij.openapi.util.*
import com.intellij.xml.util.*
import runtime.reactive.*

val application: Application
    get() = ApplicationManager.getApplication()

interface LifetimedComponent : Lifetimed, BaseComponent

class SimpleLifetimedComponent : SimpleLifetimed(), LifetimedComponent {
    override fun disposeComponent() {
        terminateLifetimeSource()
    }
}

open class SimpleLifetimed : Lifetimed {
    private val lifetimeSource = LifetimeSource()

    final override val lifetime: Lifetime get() = lifetimeSource

    protected fun terminateLifetimeSource() {
        lifetimeSource.terminate()
    }
}

class DisposableOnLifetime(lifetime: Lifetime) : Disposable {
    init {
        lifetime.add {
            Disposer.dispose(this)
        }
    }

    override fun dispose() {
    }
}

inline fun <reified T : Any> ComponentManager.getComponent(): T =
    getComponent(T::class.java) ?: throw Error("Component ${T::class.java} not found in container $this")

inline fun <reified T : Any> Project.getService(): T = service<T>().checkService(this)

inline fun <reified T : Any> T?.checkService(container: Any): T =
    this ?: throw Error("Service ${T::class.java} not found in container $container")

fun Notification.notify(lifetime: Lifetime, project: Project?) {
    lifetime.add { expire() }

    notify(project)
}

inline fun <T : Any, C : ComponentManager> C.computeSafe(crossinline compute: C.() -> T?): T? =
    application.runReadAction(Computable {
        if (isDisposed) null else compute()
    })

fun notify(lifetime: Lifetime, text: String, handler: (() -> Unit)? = null) {
    Notification(
        "Circlet",
        "Circlet",
        XmlStringUtil.wrapInHtml(text),
        NotificationType.INFORMATION,
        handler?.let { NotificationListener { _, _ -> it() } }
    ).notify(lifetime, null)
}
