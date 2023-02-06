package md.edi.mobilewaiter.common.delegates

abstract class DelegateAdapterItem(val content : Item) {
    abstract fun id(): Any

    abstract fun payload(other : DelegateAdapterItem): List<Payloadable>

    /**
     * Simple marker interface for payloads
     */
    interface Payloadable {
        object None: Payloadable
    }
}

interface Item {
    override fun equals(other: Any?): Boolean
}

abstract class ItemWithError(open var error : String?) : Item
