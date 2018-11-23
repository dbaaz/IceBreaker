<?xml version="1.0" encoding="utf-8"?>

<#if features != 'banner' && isCardView>

<android.support.v7.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    app:cardUseCompatPadding="true"
    android:layout_marginLeft="10dp"
    android:layout_marginRight="10dp"
    android:layout_marginTop="5dp"
    android:layout_marginBottom="5dp">

</#if>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="#FFFFFF"
    android:gravity="center_vertical"
    android:orientation="horizontal"
    android:padding="12dp">

    <ImageView
        android:id="@+id/img_user"
        android:layout_width="60dp"
        android:layout_height="60dp"
        android:background="#16000000"/>

 <#if features == 'radio' || features == 'checkbox' || features == 'toggle'>

  <LinearLayout
      android:layout_width="match_parent"
      android:layout_height="wrap_content"
      android:gravity="center_vertical"
      android:orientation="horizontal">

  </#if>
          <LinearLayout
              android:layout_width="0dp"
              android:layout_height="wrap_content"
              android:layout_weight="1"
              <#if features == 'radio' || features == 'checkbox' || features == 'toggle'>
              android:paddingLeft="16dp"
              android:paddingRight="16dp"
              <#else>
              android:layout_marginLeft="16dp"
              </#if>
              android:gravity="center_vertical"
              android:orientation="vertical">


              <TextView
                  android:id="@+id/item_txt_title"
                  android:layout_width="match_parent"
                  android:layout_height="wrap_content"
                  android:text=""
                  android:textColor="#212121"
                  android:textSize="14dp" />

              <TextView
                  android:id="@+id/item_txt_message"
                  android:layout_width="match_parent"
                  android:layout_height="wrap_content"
                  android:layout_marginTop="8dp"
                  android:text=""
                  android:textColor="#727272"
                  android:textSize="14dp" />

                </LinearLayout>

<#if features == 'radio'>

        <RadioButton
            android:id="@+id/radio_list"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text=""/>

    </LinearLayout>


<#elseif features == 'checkbox'>

<CheckBox
       android:id="@+id/check_list"
       android:layout_width="wrap_content"
       android:layout_height="wrap_content"/>

    </LinearLayout>

    <#elseif features == 'toggle'>

    <android.support.v7.widget.SwitchCompat
          android:id="@+id/switch_list"
          android:layout_width="wrap_content"
          android:layout_height="wrap_content"/>
        </LinearLayout>

</#if>


</LinearLayout>

<#if features != 'banner' && isCardView>

</android.support.v7.widget.CardView>

</#if>
