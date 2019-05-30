<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Necesidad;
use Response;

class NecesidadController extends Controller
{
    //Mostrar todas las necesidades
    public function index() {
      $necesidad=Necesidad::all();
      return Response::json($necesidad, 200);
    }

    //Devolver una necesidad por su id
    public function show($idNecesidad) {
      $dato=Necesidad::find($idNecesidad);
      return Response::json($dato, 200);
    }

    /*
    public function show($idUsuario) {
      $dato=Usuario::find($idUsuario);
      $nombre = $dato->nombre;
      return Response::json($nombre, 200);
    }
    */


    /*
    public function create(Request $request) {


    }*/
    /*
    public function store(Request $request) {

      $usu = new Usuario();
      $usu->nombre = $request->nombre;
      $usu->usuario = $request->usuario;
      $usu->contraseña = $request->contraseña;
      $usu->edad = $request->edad;
      $usu->foto = $request->foto;

      $usu->save();
      $usua=new Usuario();
      $usua->idUsuario = $usu->idUsuario;
      $usua->nombre = $usu->nombre;
      $usua->usuario = $usu->usuario;
      $usua->contraseña = $usu->contraseña;
      $usua->edad = $usu->edad;
      return Response::json($usua, 201);

    }
    */
    /*
    public function update(Request $request, $idUsuario) {
      $usuario=Usuario::find($idUsuario);

      $usuario->nombre = $request->nombre;
      $usuario->usuario = $request->usuario;
      $usuario->contraseña = $request->contraseña;
      $usuario->edad = $request->edad;
      $usuario->foto = $request->foto;
      $usuario->save();
      return Response::json($usuario, 201);

    }
    */
    /*
    public function destroy($idUsuario) {
      $dato=Usuario::find($idUsuario);
      $dato->delete();
      return $dato;
    }
    */


}
