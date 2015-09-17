package com.sinovoice.jTTS;

public class jTTS_JavaFastSyn
{
		public native int jTTS_PlayToFile(byte[] byText, String strTextType, String strCodePage,
			                                String strVoiceFile, String strFormat, String strFileHeadFlag,
			                                String strSyncFlag, String strVoiceName, String strDomain, 
			                                int nVolume, int nSpeed, int nPitch, 
			                                String strPuncMode, String strDigitMode, String strEngMode,
			                                String strTagMode, String strVoiceStyle,
			                                int nBackAudio, int nBackAudioVolume, String strRepeatFlag
			                                );
                               
		public native int jTTS_SetParam(String strTextType, String strVoiceName, String strFormat,
	                                  String strFileHeadFlag, String strCodePage, String strDomain,
	                                  String strSyncFlag, int nVolume, int nSpeed, int nPitch, 
	                                  String strPuncMode, String strDigitMode, String strEngMode,
	                                  String strTagMode, String strVoiceStyle,
	                                  int nBackAudio, int nBackAudioVolume, String strRepeatFlag
	                                  );
	                                   
		public native int jTTS_SetLog(String strLogPath, String strLogLevel, int nLogSize, int nLogCnt);
		
		static
		{ 
				System.loadLibrary("jTTS_JavaFastSyn");
		}
}