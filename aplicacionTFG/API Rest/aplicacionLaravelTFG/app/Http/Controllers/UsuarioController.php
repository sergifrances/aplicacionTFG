<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Usuario;
use Response;

class UsuarioController extends Controller
{

    //Devolver todos usuarios
    public function index() {
      $usuario=Usuario::all();
      return Response::json($usuario, 200);
    }

    //Devolver un usuario por su id
    public function show($idUsuario) {
      $dato=Usuario::find($idUsuario);
      //Obtener necesidades del usuario
      $necesidades = $dato->necesidades()->get();
      $dato->necesidades = $necesidades;
      return Response::json($dato, 200);
    }

    //Guardar usuario enviado en la request, en la base de datos
    public function store(Request $request) {
      //Creamos variable tipo usuario y gurdamos lo enviado desde el cliente
      $usu = new Usuario();
      $usu->nombre = $request->nombre;
      $usu->usuario = $request->usuario;
      $usu->contraseña = $request->contraseña;
      $usu->edad = $request->edad;
      $usu->mostrar = $request->mostrar;
      $usu->foto = $request->foto;
      //Guardamos en bd
      $usu->save();

      //Guardamos en array las necesidades del usuario
      $id = $request->necesidades;
      $a = array();
      foreach ($id as $value) {
        $a[] = $value["idNecesidad"];
      }
      //Añadimos en la tabla entre usuarios y necesidades ('usuario_has_necesidad'), las necesidades enviadas por cliente
      for($i=0; $i<count($a); $i++) {
        $usu->necesidades()->attach($a[$i]);
      }
      //Devolvemos el usuario creado
      $usua=new Usuario();
      $usua->idUsuario = $usu->idUsuario;
      $usua->nombre = $usu->nombre;
      $usua->usuario = $usu->usuario;
      $usua->contraseña = $usu->contraseña;
      $usua->mostrar = $usu->mostrar;
      $usua->edad = $usu->edad;
      return Response::json($usua, 201);

    }

    //Actualizar usuario
    public function update(Request $request, $idUsuario) {
      //Buscamos el usuario a modificar
      $usuario=Usuario::find($idUsuario);
      //Cambiamos los valores antiguos por los nuevos
      $usuario->nombre = $request->nombre;
      $usuario->usuario = $request->usuario;
      $usuario->contraseña = $request->contraseña;
      $usuario->edad = $request->edad;
      $usuario->mostrar = $request->mostrar;
      $usuario->foto = $request->foto;
      //Guardamos en bd
      $usuario->save();
      //Eliminamos de la tabla 'usuario_has_necesidad' las necesidades que tenía
      $usuario->necesidades()->detach();
      $id = $request->necesidades;
      $a = array();
      foreach ($id as $value) {
        $a[] = $value["idNecesidad"];
      }
      //Añadimos las nuevas necesidades seleccionadas por el usuario
      for($i=0; $i<count($a); $i++) {
        $usuario->necesidades()->attach($a[$i]);
      }

      return Response::json($usuario, 201);

    }

    //Eliminar un usuario
    public function destroy($idUsuario) {
      $dato=Usuario::find($idUsuario);
      $dato->delete();
      return $dato;
    }

    //Buscar un usuario con login y contraseña
    public function buscarUsuario($usuario, $contrasenya) {
      //Buscamos un usuario que tenga usuario y contraseña enviados por cliente
      $user=Usuario::where('usuario', $usuario)->where('contraseña', $contrasenya)->get();
      return Response::json($user, 200);
    }

    //Buscar si existe usuario introducido en el cliente, para el registro
    public function buscarUsuarioReg($usuario) {
      $user=Usuario::where('usuario', $usuario)->get();
      return Response::json($user, 200);
    }

    //Devolver id de un usurio, pasado el usuario
    public function devolverIdUsuario($usuario) {
      $user=Usuario::where('usuario', $usuario)->get();
      //return $user;
      //$usu = new Usuario();
      //$usu = $user;
      $dato=$user[0]->idUsuario;
      return Response::json($dato, 200);
    }

}
