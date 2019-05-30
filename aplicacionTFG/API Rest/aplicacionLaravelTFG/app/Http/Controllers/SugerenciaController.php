<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Sugerencia;
use Response;

class SugerenciaController extends Controller
{
  //Mostrar todas las sugerencias
  public function index() {
    $sugerencia = Sugerencia::all();
    return Response::json($sugerencia, 201);
  }

  //Guardar una sugerencia en la bd
  public function store(Request $request) {
    $sugerencia = new Sugerencia();
    //$sugerencia->id = $request->id;
    $sugerencia->sugerencia = $request->sugerencia;
    $sugerencia->save();

    return Response::json($sugerencia, 201);
  }

}
