package com.cuichen.jt808_sdk.oksocket.client.impl.client.action;

import com.cuichen.jt808_sdk.oksocket.client.sdk.client.ConnectionInfo;
import com.cuichen.jt808_sdk.oksocket.client.sdk.client.OkSocketOptions;
import com.cuichen.jt808_sdk.oksocket.client.sdk.client.action.ISocketActionListener;
import com.cuichen.jt808_sdk.oksocket.client.sdk.client.connection.IConnectionManager;
import com.cuichen.jt808_sdk.oksocket.core.iocore.interfaces.IPulseSendable;
import com.cuichen.jt808_sdk.oksocket.core.iocore.interfaces.ISendable;
import com.cuichen.jt808_sdk.oksocket.core.iocore.interfaces.IStateSender;
import com.cuichen.jt808_sdk.oksocket.core.pojo.OriginalData;
import com.cuichen.jt808_sdk.oksocket.core.utils.SLog;
import com.cuichen.jt808_sdk.oksocket.interfaces.basic.AbsLoopThread;
import com.cuichen.jt808_sdk.oksocket.interfaces.common_interfacies.dispatcher.IRegister;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static com.cuichen.jt808_sdk.oksocket.client.sdk.client.action.IAction.ACTION_CONNECTION_SUCCESS;
import static com.cuichen.jt808_sdk.oksocket.client.sdk.client.action.IAction.ACTION_WRITE_THREAD_START;
import static com.cuichen.jt808_sdk.oksocket.core.iocore.interfaces.IOAction.ACTION_PULSE_REQUEST;
import static com.cuichen.jt808_sdk.oksocket.core.iocore.interfaces.IOAction.ACTION_READ_COMPLETE;
import static com.cuichen.jt808_sdk.oksocket.core.iocore.interfaces.IOAction.ACTION_WRITE_COMPLETE;
import static com.cuichen.jt808_sdk.oksocket.client.sdk.client.action.IAction.ACTION_CONNECTION_FAILED;
import static com.cuichen.jt808_sdk.oksocket.client.sdk.client.action.IAction.ACTION_DISCONNECTION;
import static com.cuichen.jt808_sdk.oksocket.client.sdk.client.action.IAction.ACTION_READ_THREAD_SHUTDOWN;
import static com.cuichen.jt808_sdk.oksocket.client.sdk.client.action.IAction.ACTION_READ_THREAD_START;
import static com.cuichen.jt808_sdk.oksocket.client.sdk.client.action.IAction.ACTION_WRITE_THREAD_SHUTDOWN;


/**
 * ?????????
 * Created by didi on 2018/4/19.
 */
public class ActionDispatcher implements IRegister<ISocketActionListener, IConnectionManager>, IStateSender {
    /**
     * ??????????????????Handler
     */
    private static final DispatchThread HANDLE_THREAD = new DispatchThread();

    /**
     * ??????????????????
     */
    private static final LinkedBlockingQueue<ActionBean> ACTION_QUEUE = new LinkedBlockingQueue();

    static {
        //??????????????????
        HANDLE_THREAD.start();
    }

    /**
     * ??????????????????
     */
    private volatile List<ISocketActionListener> mResponseHandlerList = new ArrayList<>();
    /**
     * ????????????
     */
    private volatile ConnectionInfo mConnectionInfo;
    /**
     * ???????????????
     */
    private volatile IConnectionManager mManager;
    /**
     * ?????????,????????????????????????,???????????????tryLock
     */
    private ReentrantLock mLock = new ReentrantLock(true);


    public ActionDispatcher(ConnectionInfo info, IConnectionManager manager) {
        mManager = manager;
        mConnectionInfo = info;
    }

    @Override
    public IConnectionManager registerReceiver(final ISocketActionListener socketResponseHandler) {
        if (socketResponseHandler != null) {
            try {
                while (true) {
                    if (mLock.tryLock(1, TimeUnit.SECONDS)) {
                        if (!mResponseHandlerList.contains(socketResponseHandler)) {
                            mResponseHandlerList.add(socketResponseHandler);
                        }
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mLock.unlock();
            }
        }
        return mManager;
    }

    @Override
    public IConnectionManager unRegisterReceiver(ISocketActionListener socketResponseHandler) {
        if (socketResponseHandler != null) {
            try {
                while (true) {
                    if (mLock.tryLock(1, TimeUnit.SECONDS)) {
                        mResponseHandlerList.remove(socketResponseHandler);
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mLock.unlock();
            }
        }
        return mManager;
    }

    /**
     * ?????????????????????
     *
     * @param action
     * @param arg
     * @param responseHandler
     */
    private void dispatchActionToListener(String action, Serializable arg, ISocketActionListener responseHandler) {
        switch (action) {
            case ACTION_CONNECTION_SUCCESS: {
                try {
                    responseHandler.onSocketConnectionSuccess(mConnectionInfo, action);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_DISCONNECTION: {
                try {
                    Exception exception = (Exception) arg;
                    responseHandler.onSocketDisconnection(mConnectionInfo, action, exception);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_CONNECTION_FAILED: {
                try {
                    Exception exception = (Exception) arg;
                    responseHandler.onSocketConnectionFailed(mConnectionInfo, action, exception);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_READ_COMPLETE: {
                try {
                    OriginalData data = (OriginalData) arg;
                    responseHandler.onSocketReadResponse(mConnectionInfo, action, data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_READ_THREAD_START:
            case ACTION_WRITE_THREAD_START: {
                try {
                    responseHandler.onSocketIOThreadStart(action);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_WRITE_COMPLETE: {
                try {
                    ISendable sendable = (ISendable) arg;
                    responseHandler.onSocketWriteResponse(mConnectionInfo, action, sendable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_WRITE_THREAD_SHUTDOWN:
            case ACTION_READ_THREAD_SHUTDOWN: {
                try {
                    Exception exception = (Exception) arg;
                    responseHandler.onSocketIOThreadShutdown(action, exception);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_PULSE_REQUEST: {
                try {
                    IPulseSendable sendable = (IPulseSendable) arg;
                    responseHandler.onPulseSend(mConnectionInfo, sendable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void sendBroadcast(String action, Serializable serializable) {
        OkSocketOptions option = mManager.getOption();
        if (option == null) {
            return;
        }
        OkSocketOptions.ThreadModeToken token = option.getCallbackThreadModeToken();
        if (token != null) {
            ActionBean bean = new ActionBean(action, serializable, this);
            ActionRunnable runnable = new ActionRunnable(bean);
            try {
                token.handleCallbackEvent(runnable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (option.isCallbackInIndependentThread()) {//????????????????????????
            ActionBean bean = new ActionBean(action, serializable, this);
            ACTION_QUEUE.offer(bean);
        } else if (!option.isCallbackInIndependentThread()) {//IO?????????????????????
            try {
                while (true) {
                    if (mLock.tryLock(1, TimeUnit.SECONDS)) {
                        List<ISocketActionListener> copyData = new ArrayList<>(mResponseHandlerList);
                        Iterator<ISocketActionListener> it = copyData.iterator();
                        while (it.hasNext()) {
                            ISocketActionListener listener = it.next();
                            this.dispatchActionToListener(action, serializable, listener);
                        }
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mLock.unlock();
            }
        } else {
            SLog.e("ActionDispatcher error action:" + action + " is not dispatch");
        }
    }

    @Override
    public void sendBroadcast(String action) {
        sendBroadcast(action, null);
    }

    public void setConnectionInfo(ConnectionInfo connectionInfo) {
        mConnectionInfo = connectionInfo;
    }

    /**
     * ????????????
     */
    private static class DispatchThread extends AbsLoopThread {
        public DispatchThread() {
            super("client_action_dispatch_thread");
        }

        @Override
        protected void runInLoopThread() throws Exception {
            ActionBean actionBean = ACTION_QUEUE.take();
            if (actionBean != null && actionBean.mDispatcher != null) {
                ActionDispatcher actionDispatcher = actionBean.mDispatcher;
                synchronized (actionDispatcher.mResponseHandlerList) {
                    List<ISocketActionListener> copyData = new ArrayList<>(actionDispatcher.mResponseHandlerList);
                    Iterator<ISocketActionListener> it = copyData.iterator();
                    while (it.hasNext()) {
                        ISocketActionListener listener = it.next();
                        actionDispatcher.dispatchActionToListener(actionBean.mAction, actionBean.arg, listener);
                    }
                }
            }
        }

        @Override
        protected void loopFinish(Exception e) {

        }
    }

    /**
     * ????????????
     */
    protected static class ActionBean {
        public ActionBean(String action, Serializable arg, ActionDispatcher dispatcher) {
            mAction = action;
            this.arg = arg;
            mDispatcher = dispatcher;
        }

        String mAction = "";
        Serializable arg;
        ActionDispatcher mDispatcher;
    }

    /**
     * ??????????????????
     */
    public static class ActionRunnable implements Runnable {
        private ActionDispatcher.ActionBean mActionBean;

        ActionRunnable(ActionBean actionBean) {
            mActionBean = actionBean;
        }

        @Override
        public void run() {
            if (mActionBean != null && mActionBean.mDispatcher != null) {
                ActionDispatcher actionDispatcher = mActionBean.mDispatcher;
                synchronized (actionDispatcher.mResponseHandlerList) {
                    List<ISocketActionListener> copyData = new ArrayList<>(actionDispatcher.mResponseHandlerList);
                    Iterator<ISocketActionListener> it = copyData.iterator();
                    while (it.hasNext()) {
                        ISocketActionListener listener = it.next();
                        actionDispatcher.dispatchActionToListener(mActionBean.mAction, mActionBean.arg, listener);
                    }
                }
            }
        }
    }

}
