from app import db

class Propietario(db.Model):
    __tablename__ = 'Propietario'
    
    PropietarioID = db.Column(db.Integer, primary_key=True)
    Nombre = db.Column(db.String(100), nullable=False)
    Correo = db.Column(db.String(100), unique=True, nullable=False)
    Telefono = db.Column(db.String(15), nullable=True)
    CURP = db.Column(db.String(18), unique=True, nullable=True)
    Direccion = db.Column(db.Text, nullable=True)
    
    def __init__(self, Nombre, Correo, Telefono=None, CURP=None, Direccion=None):
        self.Nombre = Nombre
        self.Correo = Correo
        self.Telefono = Telefono
        self.CURP = CURP
        self.Direccion = Direccion
        
    def to_dict(self):
        return {
            'PropietarioID': self.PropietarioID,
            'Nombre': self.Nombre,
            'Correo': self.Correo,
            'Telefono': self.Telefono,
            'CURP': self.CURP,
            'Direccion': self.Direccion
        }
        
    def __repr__(self):
        return f"<Propietario {self.PropietarioID}: {self.Nombre} - {self.Correo}>"