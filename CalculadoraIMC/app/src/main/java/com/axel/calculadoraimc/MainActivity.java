package com.axel.calculadoraimc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

public class MainActivity extends AppCompatActivity {

    private Button calcButton;
    private RadioButton mujerButton;
    private RadioButton hombreButton;
    private EditText edadText;
    private EditText metrosText;
    private EditText cmText;
    private EditText pesoText;
    private TextView resultTxt;
    private AppDataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Crear instancia de la base de datos
        db = Room.databaseBuilder(getApplicationContext(),
                        AppDataBase.class, "database-name")
                .allowMainThreadQueries() // solo para pruebas
                .build();

        // Guardar usuario hardcodeado
        User user = new User("Axel", "Vargas");
        db.userDao().insert(user);

        findViews();
        setupButtonClickListener();
    }

    private void findViews() {
        resultTxt = findViewById(R.id.text_view_result);
        mujerButton = findViewById(R.id.radio_button_mujer);
        hombreButton = findViewById(R.id.radio_button_hombre);
        edadText = findViewById(R.id.edit_text_edad);
        metrosText = findViewById(R.id.edit_text_metros);
        cmText = findViewById(R.id.edit_text_cm);
        pesoText = findViewById(R.id.edit_text_peso);
        calcButton = findViewById(R.id.button_calcular);
    }

    private void setupButtonClickListener() {
        calcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Calculando..", Toast.LENGTH_SHORT).show();
                calcularIMC();
            }
        });
    }

    private void calcularIMC() {
        String edadStr = edadText.getText().toString();
        String metrosStr = metrosText.getText().toString();
        String cmStr = cmText.getText().toString();
        String pesoStr = pesoText.getText().toString();

        int edad = Integer.parseInt(edadStr);
        int metros = Integer.parseInt(metrosStr);
        double cm = Double.parseDouble(cmStr);
        int pesokg = Integer.parseInt(pesoStr);

        double alturaMetros = metros + (cm / 100);
        double imc = pesokg / (alturaMetros * alturaMetros);
        String resultado = String.format("%.2f", imc);

        // Clasificación y advertencias
        if (edad < 16) {
            if (mujerButton.isChecked()) {
                resultado += "\nPara una interpretación correcta consulta los percentiles de talla y peso para niñas.";
            } else if (hombreButton.isChecked()) {
                resultado += "\nPara una interpretación correcta consulta los percentiles de talla y peso para niños.";
            } else {
                resultado += "\nPara una interpretación correcta consulta los percentiles de talla y peso.";
            }
        } else {
            if (imc < 18.5) {
                resultado += "\nBajo peso";
            } else if (imc <= 24.9) {
                resultado += "\nPeso normal";
            } else if (imc <= 29.9) {
                resultado += "\nSobrepeso";
            } else {
                resultado += "\nObesidad";
            }
        }

        // Leer usuario desde la base de datos
        User usuario = db.userDao().findByNameDirect("Axel", "Vargas");
        if (usuario != null) {
            resultTxt.setText("Hola " + usuario.toString() + ", tu IMC es de " + resultado);
        } else {
            resultTxt.setText("Usuario no encontrado. Tu IMC es de " + resultado);
        }
    }
}
