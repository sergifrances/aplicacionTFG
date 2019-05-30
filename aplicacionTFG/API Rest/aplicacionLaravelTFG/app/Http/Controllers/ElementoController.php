<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Elemento;
use App\Usuario;
use Response;

class ElementoController extends Controller
{
    //Devolver todos elementos
    public function index() {
      $elemento=Elemento::all();
      return Response::json($elemento, 200);
    }

    //Devolver datos de un elemento
    public function show($idElemento) {
      //Buscamos en la base de datos el elemento con el id pasdo
      //por parámetro
      $dato=Elemento::find($idElemento);
      //Obtenemos las necesidades de ese elemento consultado tabla
      //de necesidades
      $necesidades = $dato->necesidades()->get();
      //añadimos a $dato las necesidades obtenidas anteriormente
      $dato->$necesidades = $necesidades;
      //Devolvemos como json ese elemento con sus necesidades
      return Response::json($dato, 200);
    }

    //Guardar un elemento enviado por el cliente
    public function store(Request $request) {
      //Creamos una variable para guardar el elemento que llega en el request
      $ele = new Elemento();
      $ele->nombre = $request->nombre;
      $ele->descripcion = $request->descripcion;
      $ele->coordenada = $request->coordenada;
      $ele->longitud = $request->longitud;
      $ele->latitud = $request->latitud;
      $ele->Usuario_idUsuario = $request->Usuario_idUsuario;
      $ele->tipo = $request->tipo;
      $ele->lugar = $request->lugar;
      $ele->accesible = $request->accesible;
      $ele->save();
      //En cuanto a las necesidades, recorremos el array con las necesidades que
      //nos lleguen en el request, las guardamos en un array y finalmente con
      //attach se guardan en la base de datos
      $id = $request->necesidades;
      $a = array();
      foreach ($id as $value) {
        $a[] = $value["idNecesidad"];
      }
      for($i=0; $i<count($a); $i++) {
        $ele->necesidades()->attach($a[$i]);
      }
      //Como confirmación devuelve el elemento con sus datos
      $elem=new Elemento();
      $elem->idElemento = $ele->idElemento;
      $elem->nombre = $ele->nombre;
      $elem->descripcion = $ele->descripcion;
      $elem->coordenada = $ele->coordenada;
      $elem->longitud = $ele->longitud;
      $elem->latitud = $ele->latitud;
      $elem->Usuario_idUsuario = $ele->Usuario_idUsuario;
      $elem->tipo = $ele->tipo;
      $elem->lugar = $ele->lugar;
      $elem->accesible = $ele->accesible;
      //$elem->necesidades = $ele->necesidades()->get();
      return Response::json($elem, 201);

    }

    //Actualizar un elemento
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

    //Eliminar un elemento
    public function destroy($idElemento) {
      $dato=Elemento::find($idElemento);
      $dato->delete();
      return $dato;
    }

    //Confirmar un elemento por un usuario
    public function confirmacion($idUsuario, $idElemento) {
      $elemento=Elemento::find($idElemento);
      $usuario=Usuario::find($idUsuario);
      //Añadimos a la tabla entre elemento y usuario ('confirmacion'), la confirmacion de elemento por usuario
      $elemento->usuarios()->attach($usuario->idUsuario, ['Elemento_Usuario_idUsuario' => $elemento->Usuario_idUsuario]);
      $elemento->save();

      return Response::json($elemento,201);
    }

    //Quitar confirmación de un elemento
    public function eliminarConfirmacion($idUsuario, $idElemento) {
      $elemento=Elemento::find($idElemento);
      $usuario=Usuario::find($idUsuario);
      //Eliminar tabla 'confirmacion' la confirmación de un elemento por un usuario
      $elemento->usuarios()->detach($usuario->idUsuario, ['Elemento_Usuario_idUsuario' => $elemento->Usuario_idUsuario]);
      $elemento->save();

      return Response::json($elemento,201);
    }

    //Elementos a confirmar y que no han sido confirmados ya por el usuario.
    //Si han sido confirmados por el usuario, no se le volvera a mostrar a ese usuario
    //ya que sólo puede confirmarlo una vez.
    public function elementosPorConfirmar($idUsuario) {
      $elementos=Elemento::all();

      $ele = new Elemento();
      $arr = array();
      foreach($elementos as $key=>$value) {
        $elemento=Elemento::find($value->idElemento);
        //Comprobar la cantidad de confirmaciones del elemento
        $a = $elemento->usuarios()->count();
        //Comprobar si un elemento ha sido confirmado por un usuario
        $b = $elemento->usuarios()->where('Usuario_idUsuario', $idUsuario)->get();
        //Comprobamos que tenga menos de 3 confirmaciones y no haya sido confirmado por usuario, para devolver esos elementos
        if(($a < 3) && ($b->isEmpty())) {
          $arr[$key] = $elemento;
          $ele->{$key} = $value;
        }
      }
      return Response::json($ele, 201);

    }

    //Elementos que tienen mas de tres confirmaciones
    public function elementosConfirmados() {
      $elementos=Elemento::all();
      //$arr = array();
      $ele = new Elemento();
      //Recorremos todos los elementos
      foreach($elementos as $key=>$value) {
        $elemento=Elemento::find($value->idElemento);
        //Comprobar la cantidad de confirmaciones del elemento
        $a = $elemento->usuarios()->count();
        if($a >= 3) {
          //$arr[$key] = $elemento;
          $ele->{$key} = $value;
        }
      }
      return Response::json($ele, 201);
    }

    //Elementos dentro del cuadrado entre punto inicio y fin y que estén confirmados
    public function elementosInicioFin2($lonMen, $latMen, $lonMay, $latMay) {
        $elementos = Elemento::whereBetween('longitud', [$lonMen, $lonMay])->whereBetween('latitud', [$latMen, $latMay])->get();
        //Comprobamos elementos del cuadro estén confirmados
        $ele = new Elemento();
        foreach($elementos as $key=>$value) {
          $elemento=Elemento::find($value->idElemento);
          //Comprobar la cantidad de confirmaciones del elemento
          $a = $elemento->usuarios()->count();
          if($a >= 3) {
            //$arr[$key] = $elemento;
            $ele->{$key} = $value;
          }
        }
        return Response::json($ele, 201);
    }

    //Elementos con las necesidades del usuario
    public function elementosNecesidadUsuario($idUsuario) {
      $usuario=Usuario::find($idUsuario);
      $elementos=Elemento::all();
      //Necesidades del usuario
      $necesidadesUsuario = $usuario->necesidades()->get();

      //
      $ele = new Elemento();
      foreach($elementos as $key=>$value) {
        $nec = false;
        $elemento=Elemento::find($value->idElemento);
        //Comprobar la cantidad de confirmaciones del elemento
        $a = $elemento->usuarios()->count();
        //Elementos con las necesidades del usuario
        $necesidadesElemento = $elemento->necesidades()->get();
        //Comprueba si las necesidades del elemento están entre las necesidades del usuario y si es asi asignamos a nec=true
        foreach($necesidadesElemento as $key2=>$value2) {
          $e = $value2->idNecesidad;
          foreach($necesidadesUsuario as $key3=>$value3) {
            if($e == $value3->idNecesidad) {
              $nec = true;
            }
          }
        }
        //Comprobar que tiene mas de 3 confirmaciones y la nec es true
        if(($a >= 3) && ($nec == true)) {
          //$arr[$key] = $elemento;
          $ele->{$key} = $value;
        }
      }

      return Response::json($ele, 201);

    }

    //Elementos del cuadrado entre punto inicio y fin
    public function elementosInicioFin($lonMen, $latMen, $lonMay, $latMay) {
        $elementos = Elemento::whereBetween('longitud', [$lonMen, $lonMay])->whereBetween('latitud', [$latMen, $latMay])->get();
        return Response::json($elementos, 200);
    }

    //Contribución del usuario
    public function elementosConfirmadosUsuario($idUsuario) {
      $elementos=Elemento::all();

      $ele = new Elemento();
      $arr = array();
      //Recorremos elementos
      foreach($elementos as $key=>$value) {
        $elemento=Elemento::find($value->idElemento);
        //Comprobar si un elemento ha sido confirmado por un usuario
        $b = $elemento->usuarios()->where('Usuario_idUsuario', $idUsuario)->get();
        //Si ha sido confirmado por ese usuario añadimos al array de elementos que devolveremos
        if(!$b->isEmpty()) {
          $arr[$key] = $elemento;
          $ele->{$key} = $value;
        }
      }
      return Response::json($ele, 201);

    }



}
