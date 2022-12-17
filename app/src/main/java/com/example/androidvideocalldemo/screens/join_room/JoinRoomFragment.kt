package com.example.androidvideocalldemo.screens.join_room

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.androidvideocalldemo.R
import com.example.androidvideocalldemo.databinding.FragmentJoinRoomBinding
import com.example.androidvideocalldemo.isAvailableNetwork
import com.example.androidvideocalldemo.screens.call.RoomFragment
import com.example.androidvideocalldemo.showShortToast
import gcore.videocalls.meet.GCoreMeet
import java.lang.Exception

class JoinRoomFragment : Fragment (R.layout.fragment_join_room){
    private lateinit var binding : FragmentJoinRoomBinding

    override fun onViewCreated(view: View, savedInstanceState : Bundle?){
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentJoinRoomBinding.bind(view)

        if(!allPermissionGranted()){
            requestPermissions.launch(REQUIRED_PERMISSIONS)
        }

        startCamera()
        binding.toggleVideo.setOnCheckedChangeListener{_, isEnabled ->
            if(isEnabled){
                binding.cameraPreview.visibility = View.VISIBLE
            }else{
                binding.cameraPreview.visibility= View.GONE
            }

        }

        binding.joinBtn.setOnClickListener{
            if(isAvailableNetwork()){
                configureCall()
                routeToRoom()
            }else{
                showShortToast(R.string.check_internet_connection)
            }
        }
    }
    private fun startCamera(){
        val cameraProviderFuture  =ProcessCameraProvider.getInstance(requireContext())
        val cameraSelector =  CameraSelector.DEFAULT_FRONT_CAMERA

        cameraProviderFuture.addListener({
            val cameraProvider : ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
                }

            try{
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview
                )
            }catch (e: Exception){
                Log.e("CameraX", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private  fun routeToRoom() = findNavController().navigate(
        R.id.action_joinRoomFragment_to_roomFragment, bundleOf(
            RoomFragment.ENABLE_CAM to binding.toggleVideo.isChecked,
            RoomFragment.ENABLE_MIC to binding.toggleAudio.isChecked
        )
    )
    private fun configureCall(){
        GCoreMeet.instance.roomManager.displayName = binding.etUserName.text.toString()
        GCoreMeet.instance.setRoomId(binding.etRoomId.text.toString())
        GCoreMeet.instance.clientHostName = binding.etClientHostName.text.toString()

    }
    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }
    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){
        if(!allPermissionGranted()){
            requireActivity().finish()
        }
    }

    companion object{
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }
}