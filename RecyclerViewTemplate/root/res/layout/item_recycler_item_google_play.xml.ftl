<?xml version="1.0" encoding="utf-8"?>
<android.support.v7.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    app:cardUseCompatPadding="true"
    android:layout_marginLeft="16dp"
    android:layout_marginRight="8dp">


    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="?android:selectableItemBackground"
        android:gravity="center"
        android:orientation="vertical"
        android:padding="16dp">

        <ImageView
            android:id="@+id/item_card_img"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:scaleType="fitCenter"
            android:src="@mipmap/ic_launcher" />


        <TextView
            android:id="@+id/item_card_txt_title"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="4dp"
            android:gravity="center"
            android:text="Sample title"
            android:textColor="@android:color/black"
            android:textSize="14sp" />


    </LinearLayout>

</android.support.v7.widget.CardView>
