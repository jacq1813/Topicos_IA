from database import db

class Propietario(db.Model):
    __tablename__ = 'propietario'
    
    propietarioid = db.Column(db.Integer, primary_key=True)
    nombre = db.Column(db.String(100), nullable=False)
    correo = db.Column(db.String(100), unique=True, nullable=False)
    telefono = db.Column(db.String(15), nullable=True)
    curp = db.Column(db.String(18), unique=True, nullable=True)
    direccion = db.Column(db.Text, nullable=True)
    
    def __init__(self, Nombre, Correo, Telefono=None, CURP=None, Direccion=None):
        self.nombre = Nombre
        self.correo = Correo
        self.telefono = Telefono
        self.curp = CURP
        self.direccion = Direccion
        
    def to_dict(self):
        return {
            'PropietarioID': self.propietarioid,
            'Nombre': self.nombre,
            'Correo': self.correo,
            'Telefono': self.telefono,
            'CURP': self.curp,
            'Direccion': self.direccion
        }
        
    def __repr__(self):
        return f"<Propietario {self.propietarioid}: {self.nombre} - {self.correo}>"