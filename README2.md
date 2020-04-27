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


# Intro
O que seria dos dias atuais sem a camera do celular? Este item pode ser considerado como um dos mais populares para qualquer usuario,
alias, todos gostam de registrar seus momentos favoritos e essa popularidade é exponencialmente intensificada
se considerarmos  a facilidade que os aplicativos do celular dão para o compartilhamento e armazenamento de videos e fotos.
Mesmo com toda essa popularidade é raro ter a oportunidade de pegar alguma Task para implementar a câmera.

# O que vamos fazer
Neste artigo iremos aplicar a API CAMERA2 do android usando SurfaceView, TextureView e ImageReader,
estas views possuem características diferentes que iremos explicar separadamente.

# Mão na massa
Primeiro vamos intender o que precisa ser feito para que consigamos abrir a camera e
realizar as tarefaz necessárias nela.

- Adicionar um SurfaceReadyCallback
