package com.mintab.samessenger.worker

import android.content.Context
import android.util.Log
import com.google.android.material.textfield.TextInputLayout
import com.mintab.samessenger.R

private const val TAG = "CheckEditTextsState"
private const val usernameMaxChars = 17
private const val usernameMinChars = 4

class CheckTextInputs(
    private val context: Context,
    private val emailEditText: TextInputLayout?,
    private val passwordEditText: TextInputLayout?,
    private val againPasswordEditText: TextInputLayout?,
    private val usernameEditText: TextInputLayout?,
    private val usernameOrEmailEditTextInputLayout: TextInputLayout?,
    private val nameEditText: TextInputLayout?,
    private val lastnameEditText: TextInputLayout?,
    private val addressEditText: TextInputLayout?,
    private val newPasswordEditText: TextInputLayout?,
    private val postalCodeEditText: TextInputLayout?,
    private val numberEditText: TextInputLayout?
) {

    private fun isItNumeric(toCheck: String): Boolean {
        return toCheck.all { char -> char.isDigit() }
    }

    private fun checkSpaceContainingError(editText: TextInputLayout, p0: CharSequence): Boolean {
        var hasSpaceError = false
        editText.error = null
        // does it contain spaces?
        if (doesContainSpace(p0)) {
            hasSpaceError = true
            if (editText == passwordEditText || editText == newPasswordEditText) {
                editText.error =
                    context.getString(R.string.login_edittext_password_error_space)
            } else {
                editText.error =
                    context.getString(R.string.login_edittext_number_error_space)
            }
        } else {
            editText.error = null
        }
        return hasSpaceError
    }

    fun doesContainSpace(charSequence: CharSequence): Boolean {
        var hasSpace = false
        for (char in charSequence) {
            if (char == ' ') {
                hasSpace = true
                break
            }
        }
        return hasSpace
    }

    public fun checkIsUserNameValid(): Boolean {
        var isUserNameValid = false
        val inputUsername = usernameEditText!!.editText!!.text
        if (inputUsername.length > usernameMinChars) {
            if (inputUsername.length < usernameMaxChars) {
                if (!inputUsername.contains(' ')) {
                    isUserNameValid = true
                } else {
                    usernameEditText.error =
                        context.getString(R.string.login_edittext_username_space)
                }
            } else {
                Log.i(TAG, "checkIsUserNameValid: max")
                usernameEditText.error = context.getString(R.string.login_edittext_username_maximum)
            }
        } else {
            Log.i(TAG, "checkIsUserNameValid: min")
            usernameEditText.error = context.getString(R.string.login_edittext_username_minimum)
        }
        return isUserNameValid
    }

    public fun checkIsUsernameOrEmailValid(): Boolean {
        var isUsernameOrEmailValid = false
        val usernameOrEmailText = usernameOrEmailEditTextInputLayout!!.editText!!.text.toString()
        if (!usernameOrEmailText.contains(' ')) {
            if (usernameOrEmailText.length >= usernameMinChars) {
                if (usernameOrEmailText.length <= usernameMaxChars) {
                    isUsernameOrEmailValid = true
                }
            }
        }
        if (!isUsernameOrEmailValid){
            usernameOrEmailEditTextInputLayout.error = context.getString(R.string.login_edittext_username_or_email_invalid)
        }
        return isUsernameOrEmailValid
    }


    public fun checkIsPasswordValid(): Boolean {
        var isPasswordValid = false
        val p0 = passwordEditText!!.editText?.text
        if (passwordEditText.editText?.text!!.isNotEmpty()) {
            if (p0!!.length < 6) {
                passwordEditText.error =
                    context.getString(R.string.login_edittext_password_error_minimum_quantity)
            } else {
                if (!checkSpaceContainingError(passwordEditText, p0)) {
                    if (p0.length > 16) {
                        passwordEditText.error =
                            context.getString(R.string.login_edittext_password_error_maximum_quantity)
                    } else {
                        // finally is valid
                        isPasswordValid = true
                    }
                }
            }
        } else {
            // password edittext is empty
            passwordEditText.error =
                context.getString(R.string.login_edittext_password_error_minimum_quantity)
        }
        if(isPasswordValid)
            passwordEditText.error = null
        return isPasswordValid
    }

    public fun checkIsPasswordsEqual(): Boolean {
        var arePasswordsEqual = false
        if (againPasswordEditText!!.editText!!.text.toString() == passwordEditText!!.editText!!.text.toString()) {
            arePasswordsEqual = true
        } else {
            againPasswordEditText.error =
                context.getString(R.string.signup_edittext_error_password_again_equality)
        }
        return arePasswordsEqual
    }

    public fun checkIsEmailValid(): Boolean {
        var isEmailValid = false
        if (emailEditText!!.editText!!.text!!.contains("@") &&
            emailEditText.editText!!.text!!.contains(".")
        ) {
            isEmailValid = true
        }

        if (!isEmailValid) {
            emailEditText.error = context.getString(R.string.signup_edittext_error_email_invalid)
        }
        return isEmailValid
    }

    public fun checkIsNumberValid(): Boolean {
        var isNumberValid = false
        val p0 = numberEditText!!.editText!!.text
        // user is putting number
        if (numberEditText.editText!!.text.isNotEmpty()) {
            numberEditText.error = null
            if (p0?.length!! != 11) {
                numberEditText.error = context?.getString(R.string.login_edittext_number_error)
            } else {
                if (!checkSpaceContainingError(numberEditText, p0)) {
                    numberEditText.error = null
                    // is it numeric
                    if (!isItNumeric(p0.toString())) {
                        numberEditText.error =
                            context?.getString(R.string.login_edittext_number_error_not_numeric)
                    } else {
                        // finally number format is valid
                        isNumberValid = true
                    }
                }
            }
        } else {
            // number edittext is empty
            numberEditText.error = context?.getString(R.string.login_edittext_number_error)
        }

        return isNumberValid
    }

    public fun checkIsNameValid(): Boolean {
        var isValid = false
        if (nameEditText != null) {
            val nameText = nameEditText.editText?.text.toString()
            if (nameText.length < 3) {
                nameEditText.error =
                    context.getString(R.string.signup_edittext_error_name_minimum)
            } else {
                if (nameText.length > 10) {
                    nameEditText.error =
                        context.getString(R.string.signup_edittext_error_name_maximum)
                } else {
                    isValid = true
                }
            }
        }
        return isValid
    }
    public fun checkIsLastNameValid(): Boolean {
        var isValid = false
        if (lastnameEditText != null) {
            val lastnameText = lastnameEditText.editText?.text.toString()
            if (lastnameText.length < 3) {
                lastnameEditText.error =
                    context.getString(R.string.signup_edittext_error_lastname_minimum)
            } else {
                if (lastnameText.length > 10) {
                    lastnameEditText.error =
                        context.getString(R.string.signup_edittext_error_lastname_maximum)
                } else {
                    isValid = true
                }
            }
        }
        return isValid
    }

    public fun checkIsAddressValid(): Boolean {
        var isValid = false
        if (addressEditText != null) {
            val addressText = addressEditText.editText?.text.toString()
            if (addressText.length < 5) {
                //  addressEditText.error = context.getString(R.string.change_account_details_edittext_error_address_minimum)
            } else {
                if (addressText.length > 30) {
                    //  addressEditText.error = context.getString(R.string.change_account_details_edittext_error_address_maximum)
                } else {
                    addressEditText.error = null
                    isValid = true
                }
            }
        }
        return isValid
    }

    public fun checkIsPostalCodeValid(): Boolean {
        var isValid = false
        if (postalCodeEditText != null) {
            val postalCodeText = postalCodeEditText.editText?.text.toString()
            if (postalCodeText.length != 10) {
                //    postalCodeEditText.error = context.getString(R.string.change_account_details_edittext_error_postal_code)
            } else {
                postalCodeEditText.error = null
                isValid = true
            }
        }
        return isValid
    }


    public fun checkIsNewPasswordValid(): Boolean {
        var isPasswordValid = false
        val p0 = newPasswordEditText!!.editText?.text
        if (newPasswordEditText.editText?.text!!.isNotEmpty()) {
            newPasswordEditText.error = null
            if (p0!!.length < 6) {
                newPasswordEditText.error =
                    context.getString(R.string.login_edittext_password_error_minimum_quantity)
            } else {
                if (!checkSpaceContainingError(newPasswordEditText, p0)) {
                    newPasswordEditText.error = null
                    if (p0.length > 16) {
                        newPasswordEditText.error =
                            context.getString(R.string.login_edittext_password_error_maximum_quantity)
                    } else {
                        // finally is valid
                        newPasswordEditText.error = null
                        isPasswordValid = true
                    }
                }
            }
        } else {
            // new password edittext is empty
            isPasswordValid = true
        }
        return isPasswordValid
    }


}
