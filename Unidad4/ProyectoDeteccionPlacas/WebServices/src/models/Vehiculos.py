from app import db

class Vehiculos(db.Model):
    __tablename__ = 'Vehiculos'
    
    # Clave primaria segun BD
    NumPlaca = db.Column(db.String(20), primary_key=True)
    Modelo = db.Column(db.String(50), nullable=True)
    Marca = db.Column(db.String(50), nullable=True)
    Anio = db.Column(db.Integer, nullable=True)
    PropietarioID = db.Column(db.Integer, db.ForeignKey('Propietario.PropietarioID'), nullable=False)
    
    # Relacion con Propietario
    propietario = db.relationship('Propietario', backref='vehiculos')

    def __init__(self, NumPlaca, Modelo, Marca, Anio, PropietarioID):
        self.NumPlaca = NumPlaca
        self.Modelo = Modelo
        self.Marca = Marca
        self.Anio = Anio
        self.PropietarioID = PropietarioID
        
    def to_dict(self):
        return {
            'NumPlaca': self.NumPlaca,
            'Modelo': self.Modelo,
            'Marca': self.Marca,
            'Anio': self.Anio,
            'PropietarioID': self.PropietarioID
        }
        
    def __repr__(self):
        return f"<Vehiculo {self.NumPlaca} - {self.Marca} {self.Modelo} ({self.Anio})>"