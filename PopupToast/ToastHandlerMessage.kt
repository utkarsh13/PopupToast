package com.utkarshr.popup_toast

enum class ToastHandlerMessage(val value: Int) {
    MSG_SHOW(1),
    MSG_HIDE(2),
    MSG_REMOVE_TIMER(3),
    MSG_ADD_TIMER(4)
}