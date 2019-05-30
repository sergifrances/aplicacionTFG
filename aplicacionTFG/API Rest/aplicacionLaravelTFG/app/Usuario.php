<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Usuario extends Model
{
    protected $table = 'usuario';
    protected $primaryKey = 'idUsuario';

    protected $fillable=[
      'idUsuario',
      'nombre',
      'usuario',
      'contraseña',
      'edad',
      'mostrar',
      'foto'
    ];

    //Relación N:M con elementos, con un atributo extra ('Elemento_Usuario_idUsuario'), además de las cp de las tablas
    public function elementos() {
      return $this->belongsToMany('App\Elemento', 'Confirmacion')->withPivot('Elemento_Usuario_idUsuario');
    }

    //Relación N:M con necesidades
    public function necesidades() {
      return $this->belongsToMany('App\Necesidad', 'Usuario_has_necesidad');
    }
}
