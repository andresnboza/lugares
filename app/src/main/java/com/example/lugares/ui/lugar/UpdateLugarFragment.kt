package com.example.lugares.ui.lugar

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.lugares.R
import com.example.lugares.databinding.FragmentUpdateLugarBinding
import com.example.lugares.model.Lugar
import com.example.lugares.viewmodel.LugarViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddLugarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdateLugarFragment : Fragment() {
    private var _binding: FragmentUpdateLugarBinding? = null
    private val binding get() = _binding!!

    private lateinit var lugarViewModel: LugarViewModel

    private val args by navArgs<UpdateLugarFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        saveInstanceState: Bundle?
    ) : View {
        _binding = FragmentUpdateLugarBinding.inflate(inflater, container, false)

        lugarViewModel = ViewModelProvider(this).get(LugarViewModel::class.java)

        binding.lugarName.setText(args.lugar.nombre)

        binding.finalAddLugarBtn.setOnClickListener { updateLugar() }

        //binding.tvAltura.text=args.lugar.altura.toString()
        binding.tvAltura.text="1250 pies"

        //binding.tvLatitud.text=args.lugar.latitud.toString()
        binding.tvLatitud.text="9°55′57″N"

        //binding.tvLongitud.text=args.lugar.longitud.toString()
        binding.tvLongitud.text="84°04′48″O"

        binding.emailBtn.setOnClickListener { escribirCorreo() }
        binding.phoneBtn.setOnClickListener { llamarLugar() }
        binding.whatsBtn.setOnClickListener { enviarWhatsApp() }
        binding.webBtn.setOnClickListener { verWebLugar() }
        binding.locationBtn.setOnClickListener { verMapa() }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // se es el borrado
        if (item.itemId == R.id.menu_delete) {
            deleteLugar()
        }
        return  super.onOptionsItemSelected(item)
    }

    private fun updateLugar() {
        val nombre = binding.lugarName.text.toString()

        if(validation(nombre)) {
            val lugar = Lugar(args.lugar.id, nombre)
            lugarViewModel.updateLugar(lugar)
            Toast.makeText(requireContext(), getString(R.string.msg_lugar_updated), Toast.LENGTH_LONG).show()

            findNavController().navigate(R.id.action_addLugarFragment_to_nav_lugar)
        } else {
            Toast.makeText(requireContext(), getString(R.string.msg_error), Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteLugar(){
        val builder = AlertDialog.Builder(requireContext())

        builder.setPositiveButton(getString(R.string.si)) { _,_ ->
            lugarViewModel.deleteLugar(args.lugar)
            Toast.makeText(requireContext(), getString(R.string.deleted) + " ${args.lugar.nombre}", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_updateLugarFragment_to_nav_lugar)
        }

        builder.setNegativeButton(getString(R.string.no)) { _,_ -> }
        builder.setTitle(R.string.deleted)
        builder.setMessage(getString(R.string.seguroBorrar) + " ${args.lugar.nombre}?")
        builder.create().show()
    }

    private fun llamarLugar() {
        //val telefono = binding.phone.text
        val telefono = "11223344"

        if (telefono.isNotEmpty()) {

            val dialIntent = Intent(Intent.ACTION_CALL)
            dialIntent.data = Uri.parse("tel:$telefono")

            if (requireActivity().checkSelfPermission(Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
                requireActivity().requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 105)
            } else {
                requireActivity().startActivity(dialIntent)
            }

        } else {
            Toast.makeText(requireContext(), getString(R.string.msg_datos), Toast.LENGTH_SHORT).show()
        }
    }

    private fun escribirCorreo() {
        //val para = binding.email.text.toString()
        val para = "andresnboza92@gmail.com"

        if (para.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "message/rfc822"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(para))
            intent.putExtra(
                Intent.EXTRA_SUBJECT, getString(R.string.msg_saludos) + " " + binding.lugarName.text
            )
            intent.putExtra(
                Intent.EXTRA_TEXT, getString(R.string.msg_mensaje_correo)
            )
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), getString(R.string.msg_datos), Toast.LENGTH_SHORT).show()
        }
    }

    private fun enviarWhatsApp() {
        //val telefono = binding.phone.text
        val telefono = "86381731"

        if (telefono.isNotEmpty()) {

            val sendIntent = Intent(Intent.ACTION_VIEW)
            val uri = "whatsapp://send?phone=506$telefono&text=" + getString(R.string.msg_saludos)

            sendIntent.setPackage("com.whatsapp")
            sendIntent.data = Uri.parse(uri)
            startActivity(sendIntent)

        } else {
            Toast.makeText(requireContext(), getString(R.string.msg_datos), Toast.LENGTH_SHORT).show()
        }
    }

    private fun verWebLugar() {
        //val sitio = binding.webPage.text
        val sitio = "www.nacion.com"

        if (sitio.isNotEmpty()) {

            val webPage = Uri.parse("https://$sitio")
            val intent = Intent(Intent.ACTION_VIEW, webPage)
            startActivity(intent)

        } else {
            Toast.makeText(requireContext(), getString(R.string.msg_datos), Toast.LENGTH_SHORT).show()
        }
    }

    private fun verMapa() {
        //val latitud = binding.latitud.text.toString().toDouble()
        val latitud = 1.23445
        //val longitud = binding.longitud.text.toString().toDouble()
        val longitud = 1.23445

        if (latitud.isFinite() && longitud.isFinite()) {

            val location = Uri.parse("geo:$latitud,$longitud?z=18")
            val mapIntent = Intent(Intent.ACTION_VIEW, location)
            startActivity(mapIntent)

        } else {
            Toast.makeText(requireContext(), getString(R.string.msg_datos), Toast.LENGTH_SHORT).show()
        }
    }

    private fun validation (nombre: String): Boolean {
        return !(nombre.isEmpty())
    }

}