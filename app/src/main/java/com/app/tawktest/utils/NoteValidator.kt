package com.app.tawktest.utils

class NoteValidator {

    private val mIsValid = false

    fun isValid(): Boolean {
        return mIsValid
    }
    fun isValidNote(note: CharSequence?): Boolean {
        return note != null
    }

}