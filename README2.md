


O que vamos fazer
Configurar a camera com SufaceView e TextureView



Com o que e
como vamos fazer





Surfaceview

TextureView

mImageReader



Vamos começar com o SUrfaceview



Primeiramente precisamos setar algumas propiedades que serão utilizadas entre as funções  como
cameraCharacteristics, CameraManager e WindowManager(este não precisa ser utilizado caso esteja tratando direto na activity)

``    private lateinit var backCamera: String
      private lateinit var currentCameraDevice: CameraDevice
      ``

Utilizando Camera 2  //27/02

- Camera2
    - Set FlashLight
    - Verirficando Cameras

- SurfaceView
    - Rapido
    - Problemas
- TextureView
    - Performance
    - Mais trabalhoso dependendo do layout




Como fazer um detector simples de QrCode com Camera2  //27/03
Vision 19.03( Sem firebase )
    - Por que usar essa versão
    - Função
    - Usando TextureView



CameraX com MlKit  // 27/04


# Motivos
A câmera do celular pode ser considerado como um dos itens mais populares para os usuários em geral
e também é um dos periféricos que é mais levado em conta para que o usuário opte por trocar de
celular. Porém mesmo com toda essa popularidade é raro ter a oportunidade de pegar alguma
Task para implementar a câmera.



# O que vamos fazer
Neste artigo iremos aplica a API CAMERA2 do android usando SurfaceView, TextureView e ImageReader,
estes itens possuem características diferentes em processamento e visualização


#Mão na massa
Primeiro vamos intender o que precisa ser feito para que consigamos abrir a camera e
realizar as tarefaz necessárias nela.

- Adicionar um SurfaceReadyCallback
