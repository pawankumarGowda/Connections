package com.example.connections.ui.main

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.PermissionChecker
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.connections.Adapters.WifiListAdapter
import com.example.connections.R
import com.example.connections.databinding.WifiListLayoutBinding
import com.example.connections.model.WifiStation
import kotlinx.android.synthetic.main.wifi_list_layout.view.*
import java.util.*

class MainFragment : Fragment() {


    private var wifiListLayoutBinding: WifiListLayoutBinding? = null
    var fragmentView: View? = null
    private var listAdapter: WifiListAdapter? = null
    private var wifiReceiverRegistered: Boolean = false
    var wifiManager: WifiManager? = null

    companion object {
        fun newInstance() = MainFragment()
        private const val PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 120
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        wifiListLayoutBinding =
            DataBindingUtil.inflate(inflater, R.layout.wifi_list_layout, container, false)
        fragmentView = wifiListLayoutBinding?.root
        initWifi()
        initAdapter()
        setAdapter()
        return fragmentView
    }

    override fun onStart() {
        super.onStart()
        startScanning()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (wifiReceiverRegistered) {
            activity?.unregisterReceiver(wifiReceiver)
            wifiReceiverRegistered = false
        }
    }



    private fun initAdapter() {
        listAdapter = WifiListAdapter(this@MainFragment.requireActivity())
    }

    private fun setAdapter() {
        fragmentView?.wifi_list?.apply {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
            adapter = listAdapter
        }
    }
    fun initWifi() {
        wifiManager =
            context?.getSystemService(Context.WIFI_SERVICE) as WifiManager

        if (wifiManager?.isWifiEnabled == false) {
            wifiManager?.isWifiEnabled = true
        }
    }

    private val wifiReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val results = wifiManager?.scanResults as ArrayList<ScanResult>

            if (results != null) {
                listAdapter?.setAdapterList(WifiStation.newList(Collections.unmodifiableList(results)))
            }
        }
    }

    private fun startScanning() {
        if (checkPermissions()) {
            activity?.registerReceiver(
                wifiReceiver,
                IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            )
            wifiReceiverRegistered = true
            wifiManager?.startScan()
        }
    }
    private fun checkPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && PermissionChecker.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) !== PermissionChecker.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
            )
            return false
        } else {
            return true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION -> {
                startScanning()
            }
        }
    }
}
