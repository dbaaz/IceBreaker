package com.arbiter.droid.icebreakerprot1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.arbiter.droid.icebreakerprot1.Common.getDatabaseReference
import com.google.android.gms.vision.barcode.Barcode
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_qrscan.*

class QRScanActivity : AppCompatActivity() {

    private var mDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscan)
    }

    override fun onStart() {
        super.onStart()

        mDisposable = barcodeView
                .setBarcodeFormats(Barcode.QR_CODE)
                .getObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { barcode ->
                            val barCodeValue = barcode.displayValue
                            if (barCodeValue.contains("icebreaker://pubs/"))
                            {
                                val pubId = barCodeValue.substring(barCodeValue.lastIndexOf('/')+1,barCodeValue.length);
                                getDatabaseReference().child("pubs").addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(p0: DataSnapshot) {
                                        val pubList = p0.children
                                        val iterator = pubList.iterator()
                                        while(iterator.hasNext())
                                        {
                                            val pubNode = iterator.next()
                                            if(pubId.equals(pubNode.child("qr_id").getValue().toString()))
                                            {
                                                val intent = Intent(this@QRScanActivity, PubViewActivity::class.java);
                                                intent.putExtra("venname",pubNode.child("name").getValue().toString());
                                                startActivity(intent)
                                            }
                                        }
                                    }

                                    override fun onCancelled(p0: DatabaseError) {
                                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                    }
                                })
                            }

                        },
                        { throwable ->
                            //handle exceptions like no available camera for selected facing
                        })
    }

    override fun onStop() {
        super.onStop()

        mDisposable?.dispose()
    }
}