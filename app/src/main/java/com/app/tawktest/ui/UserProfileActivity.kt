package com.app.tawktest.ui

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.app.tawktest.R
import com.app.tawktest.apiCalling.RetrofitClient
import com.app.tawktest.interfaceClass.SnackRetryInterface
import com.app.tawktest.localDataBase.User
import com.app.tawktest.localDataBase.UserViewModel
import com.app.tawktest.localDataBase.UserViewModelFactory
import com.app.tawktest.response.UserDetail
import com.app.tawktest.uc.CustomDialog
import com.app.tawktest.utils.ConnectivityReceiver
import com.app.tawktest.utils.Const
import com.app.tawktest.utils.GitHubUserApplication
import com.app.tawktest.utils.Utils
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import org.jetbrains.annotations.TestOnly
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserProfileActivity : AppCompatActivity(), View.OnClickListener, SnackRetryInterface, ConnectivityReceiver.ConnectivityReceiverListener {

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((application as GitHubUserApplication).repository)
    }

    lateinit var utils: Utils
    lateinit var customDialog: CustomDialog
    lateinit var userName: String

    lateinit var userListItem :User

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        init()
        val bundle: Bundle? = intent.extras
        bundle?.apply {
            //Parcelable Data
             userListItem = (getSerializable("userData") as User?)!!
            if (userListItem != null) {
                userName= userListItem.name
                // set note on text field
                editNote.setText(userListItem.note)
//                editNote.text = userListItem.note
//               userDetailsAPITaskCall()
            }
        }
    }

    private fun init() {
        ConnectivityReceiver.connectivityReceiverListener = this
        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        utils = Utils(this)
        utils.setRetryClickListener(this)
        customDialog = CustomDialog(this)
        backIcon.setOnClickListener(this)
        btnSave.setOnClickListener(this)
    }

    // User details fetch from server
    private fun userDetailsAPITaskCall() {

        if (utils.isNetworkAvailable()) {
            customDialog.showProgressDialog()
            val searchCall: Call<UserDetail?> = RetrofitClient.getClient.userDetails("users/$userName")
            searchCall.enqueue(object : Callback<UserDetail?> {
                override fun onResponse(
                    call: Call<UserDetail?>,
                    response: Response<UserDetail?>
                ) {
                    customDialog.dismissDialog()
                    if (response.isSuccessful) {
                        val userDetail: UserDetail = response.body()!!
                        txtUserName.text = userDetail.name
                        txtFollowers.text = userDetail.followers.toString()
                        txtFollowing.text = userDetail.following.toString()
                        txtName.text = userDetail.name
                        txtBlog.text = userDetail.blog
                        txtPublicRepos.text = userDetail.public_repos.toString()
                        txtPublicGists.text = userDetail.public_gists.toString()
                        if (!userDetail.company.isNullOrBlank()) {
                            txtCompany.text = userDetail.company
                            linearCompany.visibility = View.VISIBLE
                        }
                        if (!userDetail.blog.isNullOrBlank()) {
                            linearBlog.visibility = View.VISIBLE
                        }
                        Glide.with(this@UserProfileActivity).load(userDetail.avatar_url)
                            .placeholder(
                                R.mipmap.ic_launcher
                            ).into(userImg)
                    }
                }

                override fun onFailure(call: Call<UserDetail?>, t: Throwable) {
                    customDialog.dismissDialog()
                }
            })
        } else {
            utils.showSnackBar(
                rootViewProfile,
                this,
                getString(R.string.noInternet),
                Const.noInternet,
                Const.noInternetDuration
            )
        }
    }

    @TestOnly
    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.backIcon -> {
                onBackPressed()
            }

            R.id.btnSave -> {
                // Save note on local database
                validate(editNote.text.toString())
                if (editNote.text.toString().isEmpty()){
                  utils.showAlert("Please enter your note.")
                } else {
                    val user = User(userListItem.name, userListItem.image, editNote.text.toString().trim(), userListItem.id)
                    userViewModel.update(user)
                }
            }
        }
    }

    fun validate(userName: String): String? {
        return if (userName == "") "Please enter your note" else ""
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_CANCELED) {
            return
        }
        if (requestCode == 123) {
            utils.showSnackBar(rootViewProfile, this, getString(R.string.connected), Const.success, Const.successDuration)
            userDetailsAPITaskCall() // when net connected then
        }
    }

    override fun onReTryClick(view: View?, textView: TextView?) {
        if (textView!!.text.toString().trim { it <= ' ' }.equals(
                resources.getString(R.string.setting),
                ignoreCase = true
            )) {
            startActivityForResult(Intent(Settings.ACTION_SETTINGS), 123)
        } else {
            userDetailsAPITaskCall()
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected){
//            utils.showSnackBar(rootViewProfile, this, getString(R.string.connected), Const.success, Const.successDuration)
            userDetailsAPITaskCall()// when net connected then
        }
    }
}