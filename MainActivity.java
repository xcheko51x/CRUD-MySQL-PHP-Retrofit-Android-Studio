package com.xcheko51x.ejemplo_retrofit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    EditText etIdBuscar, etId, etNombre, etTelefono;
    Button btnIdBuscar, btnEliminar, btnTodosBuscar, btnAgregar, btnEditar;

    RecyclerView rvUsuarios;
    List<Usuario> listaUsuarios = new ArrayList<>();

    AdaptadorUsuarios adaptadorUsuarios;

    Retrofit retrofit;
    APIRest api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        etIdBuscar = findViewById(R.id.etIdBuscar);
        etId = findViewById(R.id.etId);
        etNombre = findViewById(R.id.etNombre);
        etTelefono = findViewById(R.id.etTelefono);
        btnIdBuscar = findViewById(R.id.btnIdBuscar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnTodosBuscar = findViewById(R.id.btnTodosBuscar);
        btnAgregar = findViewById(R.id.btnAgregar);
        btnEditar = findViewById(R.id.btnEditar);
        rvUsuarios = findViewById(R.id.rvUsuarios);
        rvUsuarios.setLayoutManager(new GridLayoutManager(this, 1));

        retrofit = new AdaptadorRetrofit().getAdapter();
        api = retrofit.create(APIRest.class);

        getUsuarios(api);

        btnIdBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etIdBuscar.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Inserta un ID para buscar", Toast.LENGTH_SHORT).show();
                } else {
                    getUsuario(api, etIdBuscar.getText().toString());
                }
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etIdBuscar.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Inserta un ID para eliminar", Toast.LENGTH_SHORT).show();
                } else {
                    eliminarUsuario(api, etIdBuscar.getText().toString());
                }
            }
        });

        btnTodosBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUsuarios(api);
            }
        });

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etNombre.getText().toString().equals("") || etTelefono.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Se deben de llenar los campos", Toast.LENGTH_SHORT).show();
                } else {
                    agregarUsuario(api, etNombre.getText().toString(), etTelefono.getText().toString());
                }
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etId.getText().toString().equals("") || etNombre.getText().toString().equals("") || etTelefono.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Se deben de llenar los campos", Toast.LENGTH_SHORT).show();
                } else {
                    editarUsuario(api, etId.getText().toString(), etNombre.getText().toString(), etTelefono.getText().toString());
                }
            }
        });

    }

    public void getUsuario(final APIRest api, String idUsuario) {
        listaUsuarios.clear();
        Call<Usuario> call = api.obtenerUsuario(idUsuario);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                switch (response.code()) {
                    case 200:
                        listaUsuarios.add(response.body());

                        etIdBuscar.setText("");

                        adaptadorUsuarios = new AdaptadorUsuarios(MainActivity.this, listaUsuarios);
                        rvUsuarios.setAdapter(adaptadorUsuarios);

                        break;
                    case 204:
                        Toast.makeText(MainActivity.this, "No existe ese registro", Toast.LENGTH_SHORT).show();

                        etIdBuscar.setText("");

                        getUsuarios(api);
                        break;
                        
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {

            }
        });
    }

    public void getUsuarios(APIRest api) {
        listaUsuarios.clear();
        Call<List<Usuario>> call = api.obtenerUsuarios();

        call.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                listaUsuarios = new ArrayList<Usuario>(response.body());

                adaptadorUsuarios = new AdaptadorUsuarios(MainActivity.this, listaUsuarios);
                rvUsuarios.setAdapter(adaptadorUsuarios);

            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {

            }
        });
    }

    public void eliminarUsuario(final APIRest api, String idUsuario) {
        listaUsuarios.clear();

        Call<Void> call = api.eliminarUsuario(idUsuario);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()) {
                    case 200:
                        Toast.makeText(MainActivity.this, "Se elimino correctamente", Toast.LENGTH_SHORT).show();
                        etIdBuscar.setText("");
                        getUsuarios(api);
                        break;
                    case 204:
                        Toast.makeText(MainActivity.this, "No se elimino el registro", Toast.LENGTH_SHORT).show();
                        etIdBuscar.setText("");
                        break;

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    public void agregarUsuario(final APIRest api, String nombre, String telefono) {
        listaUsuarios.clear();
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setTelefono(telefono);

        Call<Void> call = api.agregarUsuario(usuario);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()) {
                    case 400:
                        Toast.makeText(MainActivity.this, "Faltaron campos.", Toast.LENGTH_SHORT).show();
                        etNombre.setText("");
                        etTelefono.setText("");
                        break;
                    case 200:
                        Toast.makeText(MainActivity.this, "Se inserto correctamente", Toast.LENGTH_SHORT).show();
                        etNombre.setText("");
                        etTelefono.setText("");
                        getUsuarios(api);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    public void editarUsuario(final APIRest api, String idUsuario, String nombre, String telefono) {
        listaUsuarios.clear();
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        usuario.setNombre(nombre);
        usuario.setTelefono(telefono);

        Call<Void> call = api.editarUsuario(usuario);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                switch (response.code()) {
                    case 400:
                        Toast.makeText(MainActivity.this, "No se puede editar.", Toast.LENGTH_SHORT).show();
                        etId.setText("");
                        etNombre.setText("");
                        etTelefono.setText("");
                        break;
                    case 200:
                        Toast.makeText(MainActivity.this, "Se edito correctamente", Toast.LENGTH_SHORT).show();
                        etId.setText("");
                        etNombre.setText("");
                        etTelefono.setText("");
                        getUsuarios(api);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}
