package com.example.sergi.aplicacion;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    public static final String URL="http://localhost/aplicacionLaravelTFG/public/api/";

    //Rutas petición API Rest
    @GET("usuarios")
    Call<List<Usuario>> listaUsuarios();

    @GET("elementos")
    Call<List<Elemento>> listaElementos();

    @GET("usuarios/{idUsuario}")
    Call<Usuario> usuarioId(@Path("idUsuario") int id);

    @POST("usuarios")
    Call<Usuario> crearUsuario(@Body Usuario usuario);

    @PUT("usuarios/{idUsuario}")
    Call<Usuario> updateUsuario(@Body Usuario usuario, @Path("idUsuario") int id);

    @GET("buscaUsuarioLogin/{usuario}/{contrasenya}")
    Call<List<Usuario>> buscarUsuario(@Path("usuario") String usuario, @Path("contrasenya") String contrasenya);

    @GET("buscaUsuarioReg/{usuario}")
    Call<List<Usuario>> buscarUsuarioReg(@Path("usuario") String usuario);

    @DELETE("usuarios/{idUsuario}")
    Call<Usuario> borrarUsuario(@Path("idUsuario") int id);

    @GET("usuarioId/{usuario}")
    Call<Integer> devolverIdUsuario(@Path("usuario") String usuario);

    @POST("elementos")
    Call<Elemento> crearElemento(@Body Elemento elemento);

    @GET("elementos/{idElemento}")
    Call<Elemento> obtenerElementoId(@Path("idElemento") int id);

    @POST("elementos/{idUsuario}/{idElemento}")
    Call<Elemento> confirmacion(@Path("idUsuario") int idU, @Path("idElemento") int idE);

    @GET("elementosPorConfirmar/{idUsuario}")
    Call<List<Elemento>> elementosPorConfirmar(@Path("idUsuario") int id);

    @GET("elementosConfirmadosUsuario/{idUsuario}")
    Call<List<Elemento>> elementosConfirmadosUsuario(@Path("idUsuario") int id);

    @GET("elementosConfirmados")
    Call<List<Elemento>> elementosConfirmados();

    @GET("elementosNecesidad/{idUsuario}")
    Call<List<Elemento>> elementosNecesidadUsuario(@Path("idUsuario") int id);

    @GET("elementosEntreSalidaYLlegada/{longMen}/{latMen}/{longMay}/{latMay}")
    Call<List<Elemento>> elementosEntreInicioYFin(@Path("longMen") Double longMen, @Path("latMen") Double latMen, @Path("longMay") Double longMay, @Path("latMay") Double latMay);

    @POST("sugerencias")
    Call<Sugerencia> crearSugerencia(@Body Sugerencia sugerencia);

    @DELETE("elementos/{idUsuario}/{idElemento}")
    Call<Elemento> eliminarConfirmacion(@Path("idUsuario") int idU, @Path("idElemento") int idE);

}
