package com.example.lugares.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.lugares.model.Lugar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class LugarDao {

    private val coleccion1 = "lugaresApp"
    private val usuario= Firebase.auth.currentUser?.email.toString()
    private val coleccion2 = "misLugares"
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }

    fun getAllData() : MutableLiveData<List<Lugar>> {
        val listaLugares = MutableLiveData<List<Lugar>>()
        firestore.collection(coleccion1).document(usuario).collection(coleccion2)
            .addSnapshotListener{ instantanea, e ->
                if (e != null) {  //Se valida si se generó algún error en la captura de los documentos
                    return@addSnapshotListener
                }
                if (instantanea != null) {  //Si hay información recuperada...
                    //Recorro la instantanea (documentos) para crear la lista de lugares
                    val lista = ArrayList<Lugar>()
                    instantanea.documents.forEach {
                        val lugar = it.toObject(Lugar::class.java)
                        if (lugar!=null) { lista.add(lugar) }
                    }
                    listaLugares.value=lista
                }
            }
        return listaLugares
    }

    fun saveLugar(lugar: Lugar) {
        val documento: DocumentReference
        if (lugar.id.isEmpty()) {  //Si id no tiene valor entonces es un documento nuevo
            documento = firestore.collection(coleccion1).document(usuario).collection(coleccion2).document()
            lugar.id = documento.id
        } else {  //si el id tiene valor... entonces el documento existe... y recupero la info de él
            documento = firestore.collection(coleccion1).document(usuario)
                .collection(coleccion2).document(lugar.id)
        }
        documento.set(lugar)
            .addOnSuccessListener { Log.d("saveLugar","Se creó o modificó un lugar") }
            .addOnCanceledListener { Log.e("saveLugar","NO se creó o modificó un lugar") }
    }

    fun deleteLugar(lugar: Lugar) {
        if (lugar.id.isNotEmpty()) {  //Si el id tiene valor... entonces podemos eliminar el lugar... porque existe...
            firestore.collection(coleccion1).document(usuario)
                .collection(coleccion2).document(lugar.id).delete()
                .addOnSuccessListener { Log.d("deleteLugar","Se elimintó un lugar") }
                .addOnCanceledListener { Log.e("deleteLugar","NO se eliminó un lugar") }
        }
    }
}