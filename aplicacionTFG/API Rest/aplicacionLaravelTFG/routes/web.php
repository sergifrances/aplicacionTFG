<?php

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/

Route::get('/', function () {
    return view('welcome');
});

/*
Route::Resource('usuarios', 'UsuarioController');
Route::get('{idUsuario}', 'UsuarioController@show');
Route::post('create', ['usuarios' => 'UsuarioController@create']);
Route::delete('delete/{idUsuario}', 'UsuarioController@destroy');
Route::put('update/{idUsuario}', 'UsuarioController@update');
*/
