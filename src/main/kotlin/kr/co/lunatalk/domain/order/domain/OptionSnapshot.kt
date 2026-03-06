package kr.co.lunatalk.domain.order.domain

import jakarta.persistence.Embeddable

@Embeddable
open class OptionSnapshot protected constructor() {

    open var color: String? = null
        protected set

    private constructor(color: String?) : this() {
        this.color = color
    }

    companion object {
        fun createOptionSnapshot(color: String?): OptionSnapshot {
            return OptionSnapshot(color)
        }
    }
}
