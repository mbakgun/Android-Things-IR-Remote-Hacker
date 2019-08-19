package com.mbakgun.things.util

import android.util.Log
import com.mbakgun.things.ui.MainActivity
import edu.cmu.pocketsphinx.Assets
import edu.cmu.pocketsphinx.Hypothesis
import edu.cmu.pocketsphinx.RecognitionListener
import edu.cmu.pocketsphinx.SpeechRecognizer
import edu.cmu.pocketsphinx.SpeechRecognizerSetup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import javax.inject.Inject

class PocketSphinx @Inject constructor(
    private val activity: MainActivity
) : RecognitionListener {

    private var recognizer: SpeechRecognizer? = null

    override fun onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech")
    }

    override fun onEndOfSpeech() {
        Log.d(TAG, "onEndOfSpeech")

        if (recognizer?.searchName != WAKEUP_SEARCH) {
            Log.i(TAG, "End of speech. Stop recognizer")
            recognizer?.stop()
        }
    }

    override fun onPartialResult(hypothesis: Hypothesis?) {
        if (hypothesis == null) {
            return
        }
        val text = hypothesis.hypstr
        if (text == ACTIVATION_KEYPHRASE) {
            Log.i(TAG, "Activation keyphrase detected during a partial result")
            recognizer?.stop()
        } else {
            Log.i(TAG, "On partial result: $text")
        }
    }

    override fun onResult(hypothesis: Hypothesis?) {
        if (hypothesis == null) {
            return
        }
        val text = hypothesis.hypstr
        Log.i(TAG, "On result: $text")
        if (ACTIVATION_KEYPHRASE == text) {
            startListeningToAction()
        } else {
            text?.apply {
                activity.onTextRecognized(this)
            }
            startListeningToActivationPhrase()
        }
    }

    override fun onError(e: Exception) {
        Log.e(TAG, "On error", e)
    }

    override fun onTimeout() {
        Log.i(TAG, "Timeout!")
        recognizer?.stop()
        startListeningToActivationPhrase()
    }

    fun runRecognizerSetup() {
        Log.d(TAG, "Recognizer setup")
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val assets = Assets(activity)
                val assetDir = assets.syncAssets()
                setupRecognizer(assetDir)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize recognizer: ${e.localizedMessage}")
            }
        }
    }

    @Throws(IOException::class)
    private fun setupRecognizer(assetsDir: File) {
        recognizer = SpeechRecognizerSetup.defaultSetup()
            .setAcousticModel(File(assetsDir, "en-us-ptm"))
            .setDictionary(File(assetsDir, "cmudict-en-us.dict"))
            .recognizer
        recognizer?.addListener(this)

        // Custom recognizer
        recognizer?.addKeyphraseSearch(WAKEUP_SEARCH, ACTIVATION_KEYPHRASE)
        recognizer?.addNgramSearch(ACTION_SEARCH, File(assetsDir, "predefined.lm.bin"))
        startListeningToAction()
    }

    private fun startListeningToActivationPhrase() {
        Log.i(TAG, "Start listening for the \"ok things\" keyphrase")
        recognizer?.startListening(WAKEUP_SEARCH)
    }

    private fun startListeningToAction() {
        Log.i(TAG, "Start listening for some actions with a 10secs timeout")
        recognizer?.startListening(ACTION_SEARCH, 10000)
    }

    companion object {
        private val TAG = PocketSphinx::class.java.simpleName
        private const val ACTIVATION_KEYPHRASE = "ok things"
        private const val WAKEUP_SEARCH = "wakeup"
        private const val ACTION_SEARCH = "action"
    }

}
