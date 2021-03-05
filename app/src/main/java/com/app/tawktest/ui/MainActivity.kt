package com.app.tawktest.ui

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.tawktest.R
import com.app.tawktest.adapter.UserListAdapter
import com.app.tawktest.apiCalling.RetrofitClient
import com.app.tawktest.interfaceClass.SnackRetryInterface
import com.app.tawktest.localDataBase.User
import com.app.tawktest.localDataBase.UserViewModel
import com.app.tawktest.localDataBase.UserViewModelFactory
import com.app.tawktest.response.UserList
import com.app.tawktest.response.UserListItem
import com.app.tawktest.uc.CustomDialog
import com.app.tawktest.utils.ConnectivityReceiver
import com.app.tawktest.utils.Const
import com.app.tawktest.utils.GitHubUserApplication
import com.app.tawktest.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.shimmer_layout_list.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MainActivity : AppCompatActivity(), SnackRetryInterface, ConnectivityReceiver.ConnectivityReceiverListener {

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory((application as GitHubUserApplication).repository)
    }
    var userListAdapter: UserListAdapter? = null
    var userListItemTemp: ArrayList<UserListItem> = ArrayList()
    var userListItem: ArrayList<User> = ArrayList()
    lateinit var utils: Utils
    lateinit var customDialog: CustomDialog
    var since: Int = 0
    var per_page: Int = 0
    private lateinit var layoutManager: LinearLayoutManager
    var doubleBackToExitPressedOnce = false
    private var apiJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        setAdapter()
    }

    private fun callApiOrNot() {
        // Fetch data from local database
        userViewModel.allWords.observe(this) {
            Log.e("TAG", "onCreate1234: ${it.size}")
            if (it.isEmpty()) {
                userListAPITaskCall()
            } else {
                userListItem.clear()
                shimmer_view_container.visibility = View.GONE
                userListItem.addAll(it)
                userListAdapter?.notifyDataSetChanged()
            }
        }
    }

    private fun init() {
        ConnectivityReceiver.connectivityReceiverListener = this
        registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        utils = Utils(this)
        utils.setRetryClickListener(this)
        customDialog = CustomDialog(this)


        // Search from local database
        editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                userListAdapter?.filter?.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun setAdapter() {
        shimmer_view_container.visibility = View.VISIBLE
        shimmer_view_container.startShimmerAnimation()
        userListAdapter = UserListAdapter(this, userListItem)
        layoutManager = LinearLayoutManager(this)
        recyclerViewUserList.layoutManager = layoutManager
        recyclerViewUserList.adapter = userListAdapter

        recyclerViewUserList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (layoutManager.findLastCompletelyVisibleItemPosition() == userListItem.size - 1) {
                    since = userListItem[userListItem.size - 1].id
                    apiJob?.cancel()
                    per_page = userListItem.size
                    userListAPITaskCall()
                }
            }
        })
    }

    // Fetch data from server api call
    private fun userListAPITaskCall() {
        if (utils.isNetworkAvailable()) {
            if (since > 0)
//                if (customDialog.isNotNull())
                    customDialog.showProgressDialog()
            val searchCall: Call<UserList?> = RetrofitClient.getClient.userList(since, per_page)
            searchCall.enqueue(object : Callback<UserList?> {
                override fun onResponse(
                    call: Call<UserList?>,
                    response: Response<UserList?>
                ) {
                    txtDataNotFound.visibility= View.GONE
                    shimmer_view_container.visibility = View.GONE
                    if (customDialog.isNotNull())
                        customDialog.dismissDialog()
                    if (response.isSuccessful) {
                        val userList: UserList = response.body()!!
                        userListItemTemp.addAll(userList)
                        for (userItem in userListItemTemp) {
                            var user = User(userItem.login, userItem.avatar_url, "", userItem.id)
                            userViewModel.insert(user)
                            userListItem.add(user)
                        }
                        userListAdapter?.notifyDataSetChanged()

                    }else{
                        utils.showToast(response.message())
                    }
                }

                override fun onFailure(call: Call<UserList?>, t: Throwable) {
                    shimmer_view_container.visibility = View.GONE
                    customDialog.dismissDialog()
                }
            })
        } else {
            if (userListItem.isEmpty()){
                shimmer_view_container.visibility = View.GONE
                txtDataNotFound.visibility= View.VISIBLE
            }else {
                utils.showSnackBar(
                    rootView,
                    this,
                    getString(R.string.noInternet),
                    Const.noInternet,
                    Const.noInternetDuration)
            }
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            finishAffinity()
            return
        }
        doubleBackToExitPressedOnce = true
        utils.showToast("Press again to exit")
        val handler = Handler()
        handler.postDelayed(Runnable {
            doubleBackToExitPressedOnce = false

        }, 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_CANCELED) {
            return
        }
        if (requestCode == 123) {
            utils.showSnackBar(
                rootView,
                this,
                getString(R.string.connected),
                Const.success,
                Const.successDuration
            )
            callApiOrNot()
        }
    }

    override fun onReTryClick(view: View?, textView: TextView?) {
        if (textView!!.text.toString().trim { it <= ' ' }.equals(
                resources.getString(R.string.setting),
                ignoreCase = true
            )
        ) {
            startActivityForResult(Intent(Settings.ACTION_SETTINGS), 123)
        } else {
            callApiOrNot()
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected){
//            utils.showSnackBar(rootViewProfile, this, getString(R.string.connected), Const.success, Const.successDuration)
            callApiOrNot()// when net connected then
        }else{
            callApiOrNot()
        }
    }
}