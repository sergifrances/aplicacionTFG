<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Sugerencia extends Model
{
    protected $table = 'sugerencias';
    protected $primaryKey = 'id';

    protected $fillable=[
      'id',
      'sugerencia'
    ];

}
