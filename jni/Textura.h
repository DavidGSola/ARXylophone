#ifndef _TEXTURA_H_
#define _TEXTURA_H_

// Include files
#include <jni.h>

// Forward declarations

/// A utility class for textures.
class Textura
{
public:

    /// Constructor
	Textura();

    /// Destructor.
    ~Textura();

    /// Returns the width of the texture.
    unsigned int getWidth() const;

    /// Returns the height of the texture.
    unsigned int getHeight() const;

    /// Create a texture from a jni object:
    static Textura* create(JNIEnv* env, jobject textureObject);
 
    /// The width of the texture.
    unsigned int mWidth;

    /// The height of the texture.
    unsigned int mHeight;

    /// The number of channels of the texture.
    unsigned int mChannelCount;

    /// The pointer to the raw texture data.
    unsigned char* mData;

    /// The ID of the texture
    unsigned int mTextureID;
    
private: 

    /// Hidden copy constructor
    Textura(const  Textura &);
           
    /// Hidden assignment operator
    Textura& operator= (const Textura &);
    
};


#endif //_TEXTURA_H_
