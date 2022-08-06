package com.example.lugares.ui.lugar

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lugares.R
import com.example.lugares.databinding.FragmentAddLugarBinding
import com.example.lugares.model.Lugar
import com.example.lugares.viewmodel.LugarViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.lugares.utiles.ImagenUtiles
import android.util.Log
import java.util.*


class AddLugarFragment : Fragment() {
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    private var _binding: FragmentAddLugarBinding? = null
    private val binding get() = _binding!!
    private lateinit var lugarViewModel: LugarViewModel
    private lateinit var imagenUtiles: ImagenUtiles
    private lateinit var tomarFotoActivity: ActivityResultLauncher<Intent>

    private var requestCode: Int = 0
    private var resultCode: Int = 0
    private val pickImage = 100
    private var imageUri: Uri? = null
    private val REQUEST_CODE = 100

    private var rutaImagen = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lugarViewModel =
            ViewModelProvider(this)[LugarViewModel::class.java]
        _binding = FragmentAddLugarBinding.inflate(inflater, container, false)

        ubicaGPS()

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference


        binding.btAgregar.setOnClickListener {
            binding.progressBar.visibility = ProgressBar.VISIBLE
            binding.msgMensaje.text = getString(R.string.msg_subiendo_audio)
            binding.msgMensaje.visibility = TextView.VISIBLE
            addLugar(this.rutaImagen)
        }

        binding.btnChooseImage.setOnClickListener {
            launchGallery()
        }

        binding.btnUploadImage.setOnClickListener {
            uploadImage()
        }

        tomarFotoActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                imagenUtiles.actualizaFoto()
            }
        }

        return binding.root
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Log.d("TAG","###################")
        //Log.d("TAG", data?.data.toString())
        //Log.d("TAG","###################")
        this.imageUri = data?.data
        this.rutaImagen = this.imageUri.toString()
        binding.imagePreview.setImageURI(this.imageUri)
    }

    private fun uploadImage(){
        if(this.imageUri != null){
            this.rutaImagen = "myImages/" + UUID.randomUUID().toString()
            val ref = storageReference?.child(this.rutaImagen)
            val uploadTask = ref?.putFile(filePath!!)
        }else{
            Toast.makeText(requireContext(), "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }


    private fun ubicaGPS() {
        val ubicacion: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {  //Si no tengo los permisos entonces los solicito
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION),105)
        } else {  //Si se tienen los permisos
            ubicacion.lastLocation.addOnSuccessListener { location: Location? ->
                if (location!=null) {
                    binding.tvLatitud.text ="${location.latitude}"
                    binding.tvLongitud.text ="${location.longitude}"
                    binding.tvAltura.text ="${location.altitude}"
                } else {
                    binding.tvLatitud.text ="0.00"
                    binding.tvLongitud.text ="0.00"
                    binding.tvAltura.text ="0.00"
                }
            }
        }

    }

    private fun addLugar(rutaImagen:String) {
        val nombre=binding.etNombre.text.toString()
        val correo=binding.etCorreo.text.toString()
        val telefono=binding.etTelefono.text.toString()
        val web=binding.etWeb.text.toString()
        val latitud = 0.0
        val longitud= 0.0
        val altura = 0.0

        if (nombre.isNotEmpty()) { //Si puedo crear un lugar
            val lugar= Lugar("",nombre,correo,telefono,web,latitud,
                longitud,altura,"",rutaImagen)
            lugarViewModel.saveLugar(lugar)
            Toast.makeText(requireContext(),getString(R.string.msg_lugar_added),Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addLugarFragment_to_nav_lugar)
        } else {  //Mensaje de error...
            Toast.makeText(requireContext(),getString(R.string.msg_data),Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}