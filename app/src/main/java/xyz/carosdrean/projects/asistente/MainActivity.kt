package xyz.carosdrean.projects.asistente

import ai.api.AIConfiguration
import ai.api.AIListener
import ai.api.android.AIService
import ai.api.model.AIError
import ai.api.model.AIResponse
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), AIListener, TextToSpeech.OnInitListener {

    val accesToken = "13c53bfc06634e44860fc72369a19b5b"
    val REQUEST = 200
    var leer: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        micButton.setColorFilter(resources.getColor(R.color.titulo))
        leer = TextToSpeech(this, this)
        validar()
        configAsistente()
        avatars()
    }

    override fun onInit(status: Int) {
    }

    override fun onResult(result: AIResponse?) {
        val respuesta = result?.result
        val escuchado = respuesta?.resolvedQuery
        val rpta = respuesta?.fulfillment?.speech
        val responder = rpta
        reemplazarTextos(escuchado, responder)
    }

    override fun onListeningStarted() {
        micButton.setColorFilter(resources.getColor(R.color.mic))
    }

    override fun onAudioLevel(level: Float) {
    }

    override fun onError(error: AIError?) {
    }

    override fun onListeningCanceled() {
    }

    override fun onListeningFinished() {
        micButton.setColorFilter(resources.getColor(R.color.titulo))
    }

    fun avatars(){
        Glide.with(this).load(R.drawable.photo).into(user)
        Glide.with(this).load(R.drawable.user).into(asist)
    }

    fun reemplazarTextos(escuchado: String?, respuesta: String?){
        tv_escuchado.text = escuchado
        tv_respuesta.text = respuesta
        hablar(respuesta)
    }

    fun hablar(respuesta: String?){
        leer?.speak(respuesta, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun configAsistente(){
        val configuracion = ai.api.android.AIConfiguration(accesToken, AIConfiguration.SupportedLanguages.Spanish,
                ai.api.android.AIConfiguration.RecognitionEngine.System)
        val service = AIService.getService(this, configuracion)
        service.setListener(this)
        micButton.setOnClickListener { service.startListening() }
    }

    fun validar(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            solicitarPermiso()
        }
    }

    fun solicitarPermiso(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST)
        }
    }


}
