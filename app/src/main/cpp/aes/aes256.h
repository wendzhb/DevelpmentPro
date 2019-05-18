#ifndef _AES256_H_
#define _AES256_H_
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#ifndef uint8_t
#define uint8_t unsigned char
#endif

#define AES_BLOCK_SIZE 16

#ifdef __cplusplus
extern "C" {
#endif

typedef struct {
    uint8_t key[32];
    uint8_t enckey[32];
    uint8_t deckey[32];
} aes256_context;

void aes256_init(aes256_context *, uint8_t * /* key */);
void aes256_done(aes256_context *);
void aes256_encrypt(aes256_context *, uint8_t * /* plaintext */);
void aes256_decrypt(aes256_context *, uint8_t *, uint8_t */* cipertext */);
void aes256_encrypt_cbc(aes256_context *, uint8_t *, int inLen, uint8_t *, uint8_t *);
void aes256_decrypt_cbc(aes256_context *, uint8_t *, int inLen, uint8_t *, uint8_t *);
//AES 固定密钥长度 不填充 返回buff
unsigned char * AES_CBC_PKCS5_Encrypt(unsigned char *, int, unsigned char *, int*);
unsigned char * AES_CBC_PKCS5_Decrypt(unsigned char *, int, unsigned char *, int*);
#ifdef __cplusplus
}
#endif

#define DUMP(s, i, buf, sz) {printf(s); \
for (i = 0; i < (sz);i++) \
printf("%02x ", buf[i]); \
printf("\n");}

#endif
