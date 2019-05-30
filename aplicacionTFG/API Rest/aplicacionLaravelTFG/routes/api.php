<?php

use Illuminate\Http\Request;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

Route::middleware('auth:api')->get('/user', function (Request $request) {
    return $request->user();
});

Route::Resource('usuarios', 'UsuarioController');
Route::get('buscaUsuarioLogin/{usuario}/{contrasenya}', 'UsuarioController@buscarUsuario');
Route::get('buscaUsuarioReg/{usuario}', 'UsuarioController@buscarUsuarioReg');
Route::get('usuarioId/{usuario}', 'UsuarioController@devolverIdUsuario');

Route::Resource('elementos', 'ElementoController');
Route::post('elementos/{idUsuario}/{idElemento}', 'ElementoController@confirmacion');
Route::delete('elementos/{idUsuario}/{idElemento}', 'ElementoController@eliminarConfirmacion');
Route::get('elementosPorConfirmar/{idUsuario}', 'ElementoController@elementosPorConfirmar');
Route::get('elementosConfirmados', 'ElementoController@elementosConfirmados');
Route::get('elementosNecesidad/{idUsuario}', 'ElementoController@elementosNecesidadUsuario');
Route::get('elementosConfirmadosUsuario/{idUsuario}', 'ElementoController@elementosConfirmadosUsuario');
Route::get('elementosEntreSalidaYLlegada/{lonMen}/{latMen}/{lonMay}/{latMay}', 'ElementoController@elementosInicioFin2');

Route::Resource('sugerencias', 'SugerenciaController');
