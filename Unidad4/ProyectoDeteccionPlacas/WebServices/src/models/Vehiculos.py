from database import db

class Vehiculos(db.Model):
    __tablename__ = 'vehiculos'
    
    numplaca = db.Column(db.String(20), primary_key=True)
    modelo = db.Column(db.String(50), nullable=True)
    marca = db.Column(db.String(50), nullable=True)
    anio = db.Column(db.Integer, nullable=True)
    propietarioid = db.Column(db.Integer, db.ForeignKey('propietario.propietarioid'), nullable=False)
    
    # Relacion con Propietario
    propietario = db.relationship('Propietario', backref='vehiculos')

    def __init__(self, NumPlaca, Modelo, Marca, Anio, PropietarioID):
        self.numplaca = NumPlaca
        self.modelo = Modelo
        self.marca = Marca
        self.anio = Anio
        self.propietarioid = PropietarioID
        
    def to_dict(self):
        return {
            'NumPlaca': self.numplaca,
            'Modelo': self.modelo,
            'Marca': self.marca,
            'Anio': self.anio,
            'PropietarioID': self.propietarioid
        }
        
    def __repr__(self):
        return f"<Vehiculo {self.numplaca} - {self.marca} {self.modelo} ({self.anio})>"