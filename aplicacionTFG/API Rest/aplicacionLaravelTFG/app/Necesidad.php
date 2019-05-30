<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Necesidad extends Model
{
    protected $table = 'necesidad';
    protected $primaryKey = 'idNecesidad';

    protected $fillable=[
      'idNecesidad',
      'nombre'
    ];

    //Relación N:M con elementos
    public function elementos() {
      return $this->belongsToMany('App\Elemento', 'Necesidad_has_elemento');
    }

    //Relación N:M con usuarios
    public function usuarios() {
      return $this->belongsToMany('App\Usuario', 'Usuario_has_necesidad');
    }
}
