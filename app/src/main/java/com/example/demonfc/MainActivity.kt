package com.example.demonfc

import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.demonfc.ui.theme.DemoNFCTheme
import com.pro100svitlo.creditCardNfcReader.CardNfcAsyncTask
import com.pro100svitlo.creditCardNfcReader.utils.CardNfcUtils

class MainActivity : ComponentActivity(), CardNfcAsyncTask.CardNfcInterface {

    var mIntentFromCreate: Boolean? = null
    var mNfcAdapter: NfcAdapter? = null
    var mCardNfcUtils: CardNfcUtils? = null
    var mCardNfcAsyncTask: CardNfcAsyncTask? = null

    var card = mutableStateOf("")
    var expiredDate = mutableStateOf("")
    var cardType = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = this.intent
        Log.d("RAWR", "intent: $intent")

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null){
            //do something if there are no nfc module on device
        } else {
            //do something if there are nfc module on device

            mCardNfcUtils = CardNfcUtils(this);
            //next few lines here needed in case you will scan credit card when app is closed
            mIntentFromCreate = true;
            onNewIntent(getIntent());
        }

        setContent {
            DemoNFCTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting(card.value, expiredDate.value, cardType.value)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mIntentFromCreate = false;
        if (mNfcAdapter != null && !mNfcAdapter!!.isEnabled()){
            //show some turn on nfc dialog here. take a look in the samle ;-)
        } else if (mNfcAdapter != null){
            mCardNfcUtils?.enableDispatch();
        }
    }

    override fun onPause() {
        super.onPause()
        if (mNfcAdapter != null) {
            mCardNfcUtils?.disableDispatch();
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (mNfcAdapter != null && mNfcAdapter!!.isEnabled) {
            mCardNfcAsyncTask = CardNfcAsyncTask.Builder(this, intent, mIntentFromCreate!!)
                .build()
        }
    }

    override fun startNfcReadCard() {
        Log.d("RAWR", "startNfcReadCard")
    }

    override fun cardIsReadyToRead() {

        card.value = mCardNfcAsyncTask!!.cardNumber
        expiredDate.value = mCardNfcAsyncTask!!.cardExpireDate
        cardType.value = mCardNfcAsyncTask!!.cardType
        Toast.makeText(this, "Данные успешно отправлены", Toast.LENGTH_SHORT).show()
        Log.d("RAWR", "cardIsReadyToRead")
    }

    override fun doNotMoveCardSoFast() {
        Log.d("RAWR", "doNotMoveCardSoFast")
    }

    override fun unknownEmvCard() {
        Log.d("RAWR", "unknownEmvCard")
    }

    override fun cardWithLockedNfc() {
        Log.d("RAWR", "cardWithLockedNfc")
    }

    override fun finishNfcReadCard() {
        Log.d("RAWR", "finishNfcReadCard")
    }
}

@Composable
fun Greeting(
    card: String,
    expiredDate: String,
    cardType: String
) {
    Column() {
        Text(text = "card $card!")
        Text(text = "expiredDate $expiredDate!")
        Text(text = "cardType $cardType!")
    }
}
