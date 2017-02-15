/* 
 * Copyright (C) 2013 - 2014 TrustKernel Team - All Rights Reserved
 *
 * This file is part of T6.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 * A full copy of license could be obtained from 
 *
 * 		http://www.trustkernel.org/license/license.txt
 *
 * Written by Wenhao Li <liwenhaosuper@gmail.com>
 *
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <jni.h>
#include <android/log.h>




#define FBMON_INIT	0x0
#define FBMON_START 0x1 
#define FBMON_STOP  0x2
#define FBMON_READ  0x3

#define LOG_TAG "TrustKernel_SecAttest"

#define printf(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG ,__VA_ARGS__)



int SecAttest_init(void) {
	printf("[ns] SecAttest init...\n");
	//signal_init();
	return 0;
}

int SecAttest_final(void) {
	printf("[ns] SecAttest_final...\n");
	return 0;
}

int SecAttest_register_view(int x, int y, int width, int height) {
	printf("[ns] SecAttest_register_view...\n");
	return 0;
}

int SecAttest_unregister_view(int x, int y, int width, int height) {
	printf("[ns] SecAttest_unregister_view...\n");
	return 0;
}




static int error = -1;
JNIEXPORT jint Java_org_ris3_zc_hello_TEEService_SecAttestInit(JNIEnv *env, jclass clazz) {
	int ret = 0;
	// already init 
	if(error != -1)
		return 1;
	ret = SecAttest_init();
	if(ret != 0)
		error = -1;
	else 
		error = 0;
	printf("init_fbmon ret: %d\n", ret);
	return ret;
}

JNIEXPORT jint Java_org_ris3_zc_hello_TEEService_SecAttestFinal(JNIEnv *env, jclass clazz)  {
	int ret = 0;
	// not init yet
	if(error == -1)
		return 1;
	ret = SecAttest_final();
	if(ret != 0)
		error = -1;
	else 
		error = 0;
	printf("final_fbmon ret: %d\n", ret);
	return ret;
}
JNIEXPORT jint Java_org_ris3_zc_hello_TEEService_SecAttestRegisterView(JNIEnv *env, jclass clazz , jint x, jint y, jint w, jint h) {
	int ret = 0;
	// not init yet
	error = SecAttest_register_view(x, y, w, h);
	printf("register ret: %d\n", ret);
	return error;
}
JNIEXPORT jint Java_org_ris3_zc_hello_TEEService_ecAttestUnregisterView(JNIEnv *env, jclass clazz , jint x, jint y, jint w, jint h) {
	int ret = 0;
	// not init yet
	error = SecAttest_unregister_view(x, y, w, h);
	printf("unregister ret: %d\n", ret);
	return error;
}



//////////////////////////////////////////////////import MonServices func


//////////////////////////////////////////////////////////

/*
int main(int argc, char **argv){
	//filename = "/system/trustlets/_tui";
	char *data;
	int ret;

	data = (char*)malloc(4096);
	if(data == 0) {
		printf("out of mem\n");
		return 0;
	}
	memset(data, 0, 4096);

	ret = init_fbmon();
	if(ret != 0) {
		printf("failed to init fbmon\n");
		return 0;
	}

	ret = start_fbmon(0, 0, 20, 20);
	if(ret != 0) {
		printf("failed to start fbmon\n");
		return 0;
	}

	while(1) {
		ret = read_fbmon(data);
		if(ret != 0) {
			printf("failed to read fbmon\n");
			stop_fbmon();
			break;
		}
		if(*(int*)data != 0) {
			printf("data read: 0x%x\n", *(int*)data);
			FILE *f = fopen("/sdcard/fbmon.rgb", "wb");
			if(f == 0) {
				printf("failed to open target file\n");
				stop_fbmon();
				break;
			}
			fwrite(data, 4096, 1, f);
			fclose(f);
			stop_fbmon();
			break;
		}
		sleep(3);
	}
	ret = final_fbmon();
	printf("[ns] fbmon return: %x\n", ret);
	return 0;
}

*/
