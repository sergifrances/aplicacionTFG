<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Elemento extends Model
{
    protected $table = 'elemento';
    protected $primaryKey = 'idElemento';

    protected $fillable=[
      'idElemento',
      'nombre',
      'descripcion',
      'coordenada',
      'longitud',
      'latitud',
      'Usuario_idUsuario',
      'lugar',
      'tipo',
      'accesible'
    ];

    //Relación N:M con usuarios, con un atributo extra ('Elemento_Usuario_idUsuario'), además de las cp de las tablas
    public function usuarios() {
      return $this->belongsToMany('App\Usuario', 'Confirmacion')->withPivot('Elemento_Usuario_idUsuario');
    }

    //Relación N:M con necesidades
    public function necesidades() {
      return $this->belongsToMany('App\Necesidad', 'Necesidad_has_elemento');
    }

    //Relación N:M con confirmación, seleccionando valor cantidad confirmaciones, agrupando por elementos
    public function cantidadConfirmaciones() {
      return $this->belongsToMany('App\Usuario', 'Confirmacion')->withPivot('Elemento_Usuario_idUsuario')->selectRaw('count(confirmacion.Elemento_idElemento) as aggregate')->groupBy('Elemento_idElemento');
    }
}
