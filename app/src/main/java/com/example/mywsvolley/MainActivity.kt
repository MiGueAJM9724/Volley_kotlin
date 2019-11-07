package com.example.mywsvolley

import android.app.DownloadManager
import android.content.Intent
import android.location.Address
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.mywsvolley.BaseDatos.adminBD
import com.example.mywsvolley.Volley.VolleySingleton
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun getProductos(v:View){
        getAllProductos()
    }

    fun ListaProductos(v:View){
        val actividad = Intent(this,MainActivityRecycler::class.java)
        startActivity( actividad)
    }

    fun getAllProductos(){
        val wsURL = Adress.IP + "WSAndroid/getProductos.php"
        val admin = adminBD(this)
        admin.Ejecuta("DELETE FROM producto")
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST,wsURL,null,
            Response.Listener { response ->
                val succ = response["success"]
                val msg = response["message"]
                val productosJson = response.getJSONArray("productos")
                for (i in 0 until productosJson.length()){
                    val idp = productosJson.getJSONObject(i).getString("idProd")
                    val nom = productosJson.getJSONObject(i).getString("nomProd")
                    val exi = productosJson.getJSONObject(i).getString("existencia")
                    val pre = productosJson.getJSONObject(i).getString("precio")
                    val sentencia = "Insert into producto(idProd,nomProd,existencia,precio) values (${idp}, '${nom}',${exi},${pre})"
                    val res = admin.Ejecuta(sentencia)
                    Toast.makeText(this, "res= ${res}", Toast.LENGTH_SHORT).show();
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error getProductoByID: " + error.message.toString() , Toast.LENGTH_LONG).show();
                Log.d("Zazueta",error.message.toString() )
            }
        )
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }


    fun getProducto(v:View){
        if (etidProd.text.toString().isEmpty()){
            Toast.makeText(this, "Ingresa la clave del producto a buscar", Toast.LENGTH_LONG).show();
            etidProd.requestFocus()
        }
        else
        {
            var jsonEntrada= JSONObject()
            jsonEntrada.put("idProd",etidProd.text.toString())
            getProductoByID(jsonEntrada)
        }
    }

    fun getProductoByID(jsonEnt: JSONObject){
        val wsURL = Adress.IP + "WSAndroid/getProducto.php"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST,wsURL, jsonEnt,
            Response.Listener { response ->
                val succ = response["success"]
                val msg = response["message"]
                val productosJson = response.getJSONArray("producto")
                if (productosJson.length() > 0){
                    val idp = productosJson.getJSONObject(0).getString("idProd")
                    val nom = productosJson.getJSONObject(0).getString("nomProd")
                    val exi = productosJson.getJSONObject(0).getString("existencia")
                    val pre = productosJson.getJSONObject(0).getString("precio")
                    etnomProd.setText(nom)
                    etexistencia.setText(exi)
                    etprecio.setText(pre)
                    etidProd.requestFocus()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error getProductoByID: " + error.message.toString() , Toast.LENGTH_LONG).show();
                Log.d("Zazueta",error.message.toString() )
            }
        )
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }


    fun insertaProducto(v: View){
        if (etnomProd.text.toString().isEmpty() ||
                etexistencia.text.toString().isEmpty() || etprecio.text.toString().isEmpty()){
            Toast.makeText(this, "Falta información de capturar", Toast.LENGTH_SHORT).show();
            etidProd.requestFocus()
        }
        else{
            var jsonEntrada = JSONObject()
            jsonEntrada.put("nomProd", etnomProd.text.toString())
            jsonEntrada.put("existencia", etexistencia.text.toString())
            jsonEntrada.put("precio",etprecio.text.toString())
            sendRequest(Adress.IP + "WSAndroid/insertProducto.php",jsonEntrada)

        }
    }

    fun actualizarProducto(v: View){
        if (etidProd.text.toString().isEmpty() || etnomProd.text.toString().isEmpty() ||
            etexistencia.text.toString().isEmpty() || etprecio.text.toString().isEmpty()){
            Toast.makeText(this, "Falta información de capturar", Toast.LENGTH_SHORT).show();
            etidProd.requestFocus()
        }
        else{
            var jsonEntrada = JSONObject()
            jsonEntrada.put("idProd", etidProd.text.toString())
            jsonEntrada.put("nomProd", etnomProd.text.toString())
            jsonEntrada.put("existencia", etexistencia.text.toString())
            jsonEntrada.put("precio",etprecio.text.toString())
            sendRequest(Adress.IP + "WSAndroid/updateProducto.php",jsonEntrada)

        }
    }

    fun eliminarProducto(v: View){
        if (etidProd.text.toString().isEmpty()) {
            Toast.makeText(this, "Falta información de capturar", Toast.LENGTH_SHORT).show();
            etidProd.requestFocus()
        }
        else{
            var jsonEntrada = JSONObject()
            jsonEntrada.put("idProd", etidProd.text.toString())

            sendRequest(Adress.IP + "WSAndroid/deleteProducto.php",jsonEntrada)

        }
    }

    fun sendRequest( wsURL: String, jsonEnt: JSONObject){
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, wsURL,jsonEnt,
            Response.Listener { response ->
                val succ = response["success"]
                val msg = response["message"]
                Toast.makeText(this, "Success:${succ}  Message:${msg}", Toast.LENGTH_SHORT).show();
            },
            Response.ErrorListener{error ->
                Toast.makeText(this, "${error.message}", Toast.LENGTH_SHORT).show();
                Log.d("ERROR","${error.message}");
                Toast.makeText(this, "Error de capa 8 checa URL", Toast.LENGTH_SHORT).show();
            }
        )
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

}
