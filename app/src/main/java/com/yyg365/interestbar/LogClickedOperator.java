package com.yyg365.interestbar;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.yyg365.interestbar.biz.constants.EnvConstants;
import com.yyg365.interestbar.biz.tools.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogClickedOperator {

	private static final int MAX_FILE_NUM = 10;
	private static final String TAG = LogClickedOperator.class.getSimpleName();
	private static final String LOG_RECORD_FLAG = "_r";
	private static final String LOG_UPLOAD_FLAG = "_u";

	public static final String LOG_PREFIX_FORMAT = "%d_%s";
	public static final String ZIP_POSTFIX = ".gz";
	public static final String TCMS_ZIP_POSTFIX = ".tcms.gz";

	private static final String LOG_RECORD_FORMAT = LOG_PREFIX_FORMAT
			+ LOG_RECORD_FLAG;
	private static final String LOG_UPLOAD_FORMAT = LOG_PREFIX_FORMAT
			+ LOG_UPLOAD_FLAG;

	private static final int MAX_ZIP_LENGTH = 1024 * 1024 * 2;

	private static LogClickedOperator logFileOperator;
	private Context context;
	
	public synchronized static LogClickedOperator getInstance() {
		if (logFileOperator == null) {
			logFileOperator = new LogClickedOperator();
		}
		return logFileOperator;
	}
	
	public void init(Context context) {
		this.context = context;
	}

	private LogClickedOperator() {
	}
	
//	public boolean uploadFile() {
//		CommuType commuType = NetworkManager.getInstance().getLastCommuType();
//
//		if (!commuType.isValidNetworkStatus()) {
////			EventTrackManager.getEventTrack().commitEvent(TBSEventID.UPLOAD_LOGCAT_LOG_EVENT_ID, null, "uploadLogClickedNetworkIsValid",
////					null, null, null, true);
//			return false;
//		}
//
//		File logDir = getLogFileDir();
//		File[] files = null;
//		if (commuType == CommuType.COMMU_WIFI) {
//			files = logDir.listFiles(new ZipFileFilter(MAX_ZIP_LENGTH, new FileFilterCallback() {
//
//				@Override
//				public boolean callback(File ff) {
//					return ff.getName().endsWith(ZIP_POSTFIX);
//				}
//			}));
//		} else {
//			List<File> lists = new ArrayList<File>();
//			long maxSize = 1 << 17;
//			ZipFileFilter zipFileFilter = new ZipFileFilter(maxSize, new FileFilterCallback() {
//
//				@Override
//				public boolean callback(File file) {
//					return file.getName().startsWith(CLICK_HIGH_LEVEL + "") && file.getName().endsWith(ZIP_POSTFIX);
//				}
//			});
//
//			File[] highLevel = logDir.listFiles(zipFileFilter);
//			if (highLevel != null && highLevel.length > 0) {
//				//FIXME 只上传第一个。减少上传数量
////				List<File> highs = Arrays.asList(highLevel);
//
//				lists.add(highLevel[0]);
//			}
//
////			maxSize = zipFileFilter.getMaxZipLength();
////			zipFileFilter = new ZipFileFilter(maxSize, new FileFilterCallback() {
////
////				@Override
////				public boolean callback(File file) {
////					return file.getName().startsWith(CLICK_LOW_LEVEL + "") && file.getName().endsWith(ZIP_POSTFIX);
////				}
////			});
////
////			File[] lowLevel = logDir.listFiles(zipFileFilter);
////			if (lowLevel.length > 0) {
////				List<File> lows = Arrays.asList(lowLevel);
////				lists.addAll(lows);
////			}
//
//			files = lists.toArray(new File[lists.size()]);
//		}
//
//		if (files == null || files.length <= 0) {
////			EventTrackManager.getEventTrack().commitEvent(TBSEventID.UPLOAD_LOGCAT_LOG_EVENT_ID, null, "uploadLogClickedFileEmpty",
////					commuType.getType(), null, null, true);
//			return false;
//		}
//
//		boolean result = false;
//		synchronized (TAG) {
//			result = this.uploadFile(files);
//		}
//		return result;
//	}

//	@Override
//	public boolean zipFile(File logDir, final int type, boolean withCurrent) {
//		if (logDir == null || !logDir.exists()) {
////			EventTrackManager.getEventTrack().commitEvent(TBSEventID.UPLOAD_LOGCAT_LOG_EVENT_ID, null, "logFileDirNotExists",
////					logDir.getAbsolutePath(), null, null, true);
//			return false;
//		}
//		synchronized (TAG) {
//			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
//			SimpleDateFormat timeFormatter = new SimpleDateFormat(
//					"yyyyMMdd_HHmmss");
//			Date now = new Date(System.currentTimeMillis());
//			String dsp = formatter.format(now);
//			String time = timeFormatter.format(now);
//
//			File[] ll = logDir.listFiles(new FileFilter() {
//
//				@Override
//				public boolean accept(File ff) {
//					if (ff == null||!ff.exists() || ff.length() <=0) {
//						return false;
//					}
//					return ff.getName().startsWith(type + "")
//							&& ff.getName().endsWith(LOG_RECORD_FLAG);
//				}
//			});
//
//			if (ll == null || ll.length <= 0) {
////				EventTrackManager.getEventTrack().commitEvent(TBSEventID.UPLOAD_LOGCAT_LOG_EVENT_ID, null, "uploadLogClickedRecordFileEmpty",
////						null, null, null, true);
//				return false;
//			}
//
//			for (File f : ll) {
//				String name = f.getName();
//				String recordName = String.format(LOG_RECORD_FORMAT, type, dsp);
//				if (name.equals(recordName)) {
//					if (withCurrent) {
//						String uploadName = String.format(LOG_UPLOAD_FORMAT,
//								type, time);
//						FileUtils.rename(f, uploadName);
//					} else {
//						continue;
//					}
//				} else {
//					FileUtils.rename(f,
//							name.replace(LOG_RECORD_FLAG, LOG_UPLOAD_FLAG));
//				}
//			}
//
//			File[] files = logDir.listFiles(new FileFilter() {
//
//				@Override
//				public boolean accept(File file) {
//					if (file == null || !file.exists()) {
//						return false;
//					}
//					String fileName = file.getName();
//					return fileName.endsWith(LOG_UPLOAD_FLAG)
//							&& fileName.startsWith(type + "");
//				}
//			});
//
//			if (files == null || files.length <= 0) {
////				EventTrackManager.getEventTrack().commitEvent(TBSEventID.UPLOAD_LOGCAT_LOG_EVENT_ID, null, "uploadLogZipFileEmpty",
////						null, null, null, true);
//				return false;
//			}
//
//			String deviceId = SdkBaseInfoHelper.getInstance().getSdkBaseInfo().deviceId;
//			if (TextUtils.isEmpty(deviceId)) {
//				deviceId = UUID.randomUUID().toString().replace("-", "");
//			}
//
//			for (File f : files) {
//				String fileName = f.getName().replace(LOG_UPLOAD_FLAG, "_" + deviceId);
//				File to = new File(f.getParentFile().getAbsoluteFile() + File.separator + fileName + TCMS_ZIP_POSTFIX);
//				GzipUtil.compress(f, to);
//				FileUtils.deleteFile(f);
//			}
//
//		}
//		return true;
//	}
	
	public File getLogFileDir() {
		String state = Environment.getExternalStorageState();
		File dir;
		if (state != null && state.equals(Environment.MEDIA_MOUNTED)) {
			String ppath = EnvConstants.diamondPath;
			
			File pp = new File(ppath);
			
			if (!pp.exists()) {
				pp.mkdir();
			}
			
			String path = ppath + File.separator + "userTrack";
			
			dir = new File(path);

			if (!dir.exists()) {
				dir.mkdirs();
			}
		} else {
			
			File ppath = context.getFilesDir();
			String path = ppath.getAbsolutePath() + File.separator + "userTrack";
			dir = new File(path);
			if (!dir.exists()) {
				dir.mkdir();
			}
		}

		return dir;
	}

	public void writeLog(String log, final int type) {
		final File dir = getLogFileDir();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String dsp = formatter.format(new Date(System
				.currentTimeMillis()));
		
		
		String fileName = String.format(LOG_RECORD_FORMAT, type, dsp);

		File file = new File(dir.getAbsoluteFile() + File.separator + fileName);
		
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				Log.e("LogWriter", "createNewFile cause error:" + e.getMessage());
				return;
			}
			CheckLogFileNum(type, dir);
		}
		
		if (file.length() > MAX_ZIP_LENGTH) {
			SimpleDateFormat timeFormatter = new SimpleDateFormat(
					"yyyyMMdd_HHmmss");
			Date now = new Date(System.currentTimeMillis());
			String time = timeFormatter.format(now);
			String uploadName = String.format(LOG_UPLOAD_FORMAT, type, time);
			FileUtils.rename(file, uploadName);
			
			CheckLogFileNum(type, dir);
		}
		
		FileUtils.write(file, log);
	}

	private void CheckLogFileNum(final int type, final File dir) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				File[] files = dir.listFiles(new FileFilter() {
					
					@Override
					public boolean accept(File file) {
						String fileName = file.getName();
						return fileName.startsWith(type + "");
					}
				});
				
				if (files == null||files.length <= MAX_FILE_NUM) {
					return;
				}
				
				sort(files);
				for (int i = 0, length = files.length - MAX_FILE_NUM; i < length; i++) {
					if (files[i] != null) {
						files[i].delete();
					}
				}
				
			}
		}).start();
	}

	protected void sort(File[] files) {
		File temp = null;
		boolean condition = false;
		for (int i = 0; i < files.length; i++) {
			for (int j = files.length - 1; j > i; j--) {
				if (files[j] != null && files[j - 1] != null) {
					condition = files[j].lastModified() < files[j - 1]
							.lastModified();
					if (condition) {
						temp = files[j];
						files[j] = files[j - 1];
						files[j - 1] = temp;
					}
				}
			}
		}

	}
}
